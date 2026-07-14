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
    
    private ApiEndpoints() {}
}
