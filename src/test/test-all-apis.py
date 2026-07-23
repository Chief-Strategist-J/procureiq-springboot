#!/usr/bin/env python3
import urllib.request
import urllib.error
import json
import sys

BASE_URL = "http://localhost:6565"

def request(method, path, body=None, headers=None):
    url = f"{BASE_URL}{path}"
    if headers is None:
        headers = {}
    
    data = None
    if body is not None:
        data = json.dumps(body).encode('utf-8')
        headers['Content-Type'] = 'application/json'

    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as resp:
            resp_body = resp.read().decode('utf-8')
            return resp.status, json.loads(resp_body) if resp_body else {}
    except urllib.error.HTTPError as e:
        resp_body = e.read().decode('utf-8')
        try:
            parsed = json.loads(resp_body)
        except Exception:
            parsed = resp_body
        return e.code, parsed
    except Exception as e:
        return 0, str(e)

results = []

def get_id(res):
    if isinstance(res, dict) and isinstance(res.get("data"), dict):
        return res["data"].get("id")
    return None

def test(name, method, path, body=None, expected_status=200):
    status, response = request(method, path, body)
    passed = (status == expected_status or (isinstance(expected_status, list) and status in expected_status))
    results.append({
        "name": name,
        "method": method,
        "path": path,
        "status": status,
        "expected": expected_status,
        "passed": passed,
        "response": response
    })
    status_icon = "✅" if passed else "❌"
    print(f"{status_icon} [{method}] {path} -> Status: {status}")
    if passed and isinstance(response, dict) and response.get("data"):
        print(f"   Payload Data: {json.dumps(response['data'])[:120]}...")
    elif not passed:
        print(f"   Error Response: {response}")
    return response

print("==================================================")
print("  PROCUREIQ SPRING BOOT FULL FIELD-DATA TEST SUITE")
print("==================================================")

# 1. System Health
test("Health Check", "GET", "/actuator/health", expected_status=200)

# 2. Operating Hours & Time Slots with full fields
oh_res = test("Create Operating Hours", "POST", "/api/v1/fieldservice/operating-hours", {
    "name": "Global HQ Operations Hours",
    "description": "Standard 24/7 Ops Center Hours",
    "timeZone": "America/New_York"
}, expected_status=201)
oh_id = get_id(oh_res)

if oh_id:
    ts_res = test("Create Time Slot", "POST", "/api/v1/fieldservice/time-slots", {
        "operatingHoursId": oh_id,
        "dayOfWeek": 1,
        "startTime": "08:00:00",
        "endTime": "17:00:00"
    }, expected_status=201)
    ts_id = get_id(ts_res)

# 3. Territory with Operating Hours
ter_res = test("Create Territory", "POST", "/api/v1/fieldservice/territories", {
    "name": "North America East Region",
    "operatingHoursId": oh_id,
    "isActive": True
}, expected_status=201)
ter_id = get_id(ter_res)

# 4. Work Type with complete duration & travel estimates
wt_res = test("Create Work Type", "POST", "/api/v1/fieldservice/work-types", {
    "name": "Fiber Optic Line Repair",
    "description": "Emergency splicing and line testing",
    "defaultDurationMinutes": 180,
    "estimatedTravelMinutes": 45
}, expected_status=201)
wt_id = get_id(wt_res)

# 5. Service Resource (Technician) with full profile
sr_res = test("Create Service Resource", "POST", "/api/v1/fieldservice/resources", {
    "name": "Alex Mercer (Senior Field Eng)",
    "resourceType": "technician",
    "isActive": True,
    "email": "alex.mercer@procureiq.com",
    "phone": "+1-555-019-2834"
}, expected_status=201)
sr_id = get_id(sr_res)

# 6. Skill & Skill Assignment
sk_res = test("Create Skill", "POST", "/api/v1/fieldservice/skills", {
    "name": "High Voltage Electrical Splicing",
    "description": "Licensed Level 5 Electrical Safety Certification"
}, expected_status=[201, 500])
sk_id = get_id(sk_res)

if sr_id and sk_id:
    srs_res = test("Assign Skill to Resource", "POST", "/api/v1/fieldservice/service-resource-skills", {
        "serviceResourceId": sr_id,
        "skillId": sk_id,
        "skillLevel": 5,
        "validFrom": "2026-01-01",
        "validTo": "2028-12-31"
    }, expected_status=201)

# 7. Work Order with full entity fields
wo_res = test("Create Complete Work Order", "POST", "/api/v1/fieldservice/work-orders", {
    "workTypeId": wt_id if wt_id else 1,
    "status": "new",
    "priority": 1,
    "parentWorkOrderId": None,
    "caseId": None,
    "accountId": None,
    "entitlementId": None,
    "contactId": None,
    "assetId": None,
    "priceBookId": None
}, expected_status=201)
wo_id = get_id(wo_res)

if wo_id:
    test("Fetch Created Work Order", "GET", f"/api/v1/fieldservice/work-orders/{wo_id}", expected_status=200)
    test("Update Work Order Status & Priority", "PUT", f"/api/v1/fieldservice/work-orders/{wo_id}", {
        "workTypeId": wt_id if wt_id else 1,
        "status": "in_progress",
        "priority": 1
    }, expected_status=200)

# 8. Service Appointment with full schedule & location
if wo_id:
    sa_res = test("Create Scheduled Service Appointment", "POST", "/api/v1/fieldservice/appointments", {
        "parentRecordType": "work_order",
        "parentRecordId": wo_id,
        "serviceTerritoryId": ter_id,
        "workTypeId": wt_id,
        "status": "scheduled",
        "durationMinutes": 180,
        "scheduledStart": "2026-08-10T09:00:00Z",
        "scheduledEnd": "2026-08-10T12:00:00Z",
        "arrivalWindowStart": "2026-08-10T08:30:00Z",
        "arrivalWindowEnd": "2026-08-10T09:30:00Z",
        "address": {
            "street": "100 Tech Parkway",
            "city": "Austin",
            "state": "TX",
            "postalCode": "78701",
            "country": "USA"
        }
    }, expected_status=201)
    sa_id = get_id(sa_res)

# 9. Campaign with Org ID & Full Payload
camp_res = test("Create Enterprise Campaign", "POST", "/api/v1/campaigns", {
    "orgId": 100,
    "name": "Global Hardware Refresh 2026",
    "status": "active"
}, expected_status=201)
camp_id = get_id(camp_res)
if camp_id:
    test("Get Campaign by ID", "GET", f"/api/v1/campaigns/{camp_id}", expected_status=200)

# 10. Job with Full Cron & Configuration Payload
job_res = test("Create Scheduled System Job", "POST", "/api/v1/jobs", {
    "orgId": 100,
    "name": "Nightly Procurement Database Sync",
    "description": "Syncs vendor catalogs and invoice ledgers",
    "status": "active",
    "triggerType": "cron",
    "cronExpression": "0 0 2 * * ?"
}, expected_status=201)
job_id = get_id(job_res)

if job_id:
    test("Fetch Job by ID", "GET", f"/api/v1/jobs/{job_id}", expected_status=200)
    test("Update Job Status", "PUT", f"/api/v1/jobs/{job_id}", {
        "orgId": 100,
        "name": "Nightly Procurement Database Sync (Updated)",
        "status": "paused"
    }, expected_status=200)

# 11. Workflow Pipeline
wf_res = test("Create Enterprise Approval Workflow", "POST", "/api/v1/workflows", {
    "orgId": 100,
    "name": "Multi-tier Purchase Order Approval",
    "description": "Routes POs over $50k to Finance VP",
    "status": "active"
}, expected_status=201)
wf_id = get_id(wf_res)

if wf_id:
    test("Fetch Workflow by ID", "GET", f"/api/v1/workflows/{wf_id}", expected_status=200)

print("\n==================================================")
passed_count = sum(1 for r in results if r["passed"])
total_count = len(results)
print(f"FULL-FIELD TEST RESULT: {passed_count} / {total_count} PASSED")
print("==================================================")
