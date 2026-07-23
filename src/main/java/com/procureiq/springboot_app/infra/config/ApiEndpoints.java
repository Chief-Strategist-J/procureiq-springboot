package com.procureiq.springboot_app.infra.config;

public final class ApiEndpoints {
    public static final String API_V1 = "/api/v1";
    public static final String AUTH = API_V1 + "/auth";
    public static final String GITHUB = API_V1 + "/github";
    public static final String GMAIL = API_V1 + "/gmail";
    public static final String NOTIFICATIONS = API_V1 + "/notifications";
    public static final String CAMPAIGNS = API_V1 + "/campaigns";
    public static final String JOBS = API_V1 + "/jobs";
    public static final String WORKFLOWS = API_V1 + "/workflows";
    public static final String VOICE = API_V1 + "/voice";
    public static final String REMINDERS = API_V1 + "/reminders";
    public static final String CRYPTO = API_V1 + "/crypto";
    public static final String GITHUB_TEMPLATES = API_V1 + "/github/templates";
    
    public static final String FIELD_SERVICE = API_V1 + "/fieldservice";
    public static final String WORK_TYPES = FIELD_SERVICE + "/work-types";
    public static final String SERVICE_APPOINTMENTS = FIELD_SERVICE;
    public static final String SERVICE_RESOURCE_CAPACITIES = FIELD_SERVICE + "/service-resource-capacities";
    public static final String SERVICE_CREW_MEMBERS = FIELD_SERVICE + "/service-crew-members";
    public static final String RESOURCES = FIELD_SERVICE + "/resources";
    public static final String SERVICE_TERRITORY_MEMBERS = FIELD_SERVICE + "/service-territory-members";
    public static final String SKILLS = FIELD_SERVICE + "/skills";
    public static final String MILESTONES = FIELD_SERVICE + "/milestones";
    public static final String MAINTENANCE_PLANS = FIELD_SERVICE + "/maintenance-plans";
    public static final String CASE_MILESTONES = FIELD_SERVICE + "/case-milestones";
    public static final String TIME_SLOTS = FIELD_SERVICE + "/time-slots";
    public static final String RESOURCE_PREFERENCES = FIELD_SERVICE + "/resource-preferences";
    public static final String OPERATING_HOURS = FIELD_SERVICE + "/operating-hours";
    public static final String WORK_ORDERS = FIELD_SERVICE + "/work-orders";
    public static final String TERRITORIES = FIELD_SERVICE + "/territories";
    public static final String RESOURCE_ABSENCES = FIELD_SERVICE + "/resource-absences";
    public static final String SKILL_REQUIREMENTS = FIELD_SERVICE + "/skill-requirements";
    public static final String SERVICE_RESOURCE_SKILLS = FIELD_SERVICE + "/service-resource-skills";
    public static final String ASSET_RELATIONSHIPS = FIELD_SERVICE + "/asset-relationships";
    public static final String SERVICE_CREWS = FIELD_SERVICE + "/service-crews";
    public static final String SHIFTS = FIELD_SERVICE + "/shifts";

    
    public static final String PATH_ID = "/{id}";
    public static final String UNREAD_COUNT = "/unread-count";
    public static final String DEVICES = "/devices";
    public static final String STATUS_ID = "/{id}/status";
    
    public static final String SCHEDULES = "/schedules";
    public static final String SCHEDULES_ID = "/schedules/{id}";
    
    public static final String RECIPIENTS = "/recipients";
    public static final String RECIPIENTS_ID = "/recipients/{id}";
    
    public static final String DISPATCH = "/dispatch";
    public static final String REPO_INFO = "/repo-info";
    public static final String WORKFLOW_RUNS = "/workflow-runs";
    public static final String CREATE_WORKFLOW = "/create-workflow";
    public static final String DELETE_WORKFLOW = "/delete-workflow";
    
    public static final String APPOINTMENTS_SUB = "/appointments";
    public static final String APPOINTMENTS_ID = "/appointments/{id}";
    public static final String APPOINTMENTS_ASSIGN = "/appointments/{appointmentId}/assign";
    public static final String ASSIGNED_RESOURCES_ID = "/assigned-resources/{id}";
    public static final String APPOINTMENTS_CANDIDATES = "/appointments/{appointmentId}/candidates";
    
    public static final String TEMPLATES = "/templates";
    public static final String TEMPLATES_ID = "/templates/{id}";
    
    public static final String SCHEDULE = "/schedule";
    public static final String SCHEDULED = "/scheduled";
    
    public static final String SEND = "/send";
    public static final String LIST = "/list";
    public static final String RUNS = "/runs";
    public static final String SIGNUP = "/signup";
    public static final String LOGIN = "/login";
    public static final String FORGOT_PASSWORD = "/forgot-password";
    public static final String RESET_PASSWORD = "/reset-password";
    public static final String JOBS_ID_RUNS = "/{jobId}/runs";
    public static final String WORKFLOWS_ID_RUNS = "/{workflowId}/runs";
    
    private ApiEndpoints() {}
}
