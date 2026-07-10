-- migration:      0002
-- description:    complete field service core schema tables (postgreSQL)
-- author:         Antigravity
-- date:           2026-07-10
-- depends_on:     0001
-- reversible:     YES
-- lock_risk:      LOW
-- rows_affected:  schema only
-- reason:         comprehensive field service data model DDL implementation
-- nullable_columns_rationale:
--   - accounts.operating_hours_id: optional business scheduling rules
--   - contacts.email, contacts.phone: optional communication details
--   - cases.contact_id: case might be created directly against account
--   - price_book_entries: standard structure
--   - assets.product_id: asset might not be tied to catalog product
--   - assets.serial_number, assets.install_date: optional descriptors
--   - entitlements.service_contract_id: contract might not exist/be active
--   - entitlements.operating_hours_id: entitlement might use default hours
--   - entitlements.end_date: perpetual entitlement
--   - work_orders.parent_work_order_id: top level work order has no parent
--   - work_orders.case_id: direct work order creation without case
--   - work_orders.entitlement_id, work_orders.contact_id, work_orders.asset_id: optional bindings
--   - work_orders.work_type_id, work_orders.price_book_id: optional configurations
--   - work_order_line_items.parent_line_item_id: top level line item has no parent
--   - work_order_line_items.asset_id, work_order_line_items.work_type_id, work_order_line_items.price_book_entry_id: optional configuration overrides
--   - skills.description: optional skill details
--   - skill_requirements.work_type_id, skill_requirements.work_order_id, skill_requirements.work_order_line_item_id: polymorphic columns (one must be non-null)
--   - service_territories.parent_territory_id: top-level territory
--   - service_territories.operating_hours_id: territory might inherit parent hours
--   - service_resources.user_id, service_resources.service_crew_id: resource can be a user or crew (or equipment)
--   - service_crew_members.end_date: active crew members have no end date
--   - service_resource_skills.valid_to: certificate that never expires
--   - service_territory_members.operating_hours_id: defaults to territory hours if not set
--   - service_resource_capacities.end_date: perpetual capacity limits
--   - resource_absences.description: optional details
--   - service_appointments.account_id, service_appointments.contact_id: denormalized optional bindings
--   - service_appointments.service_territory_id, service_appointments.work_type_id: optional scheduling elements
--   - service_appointments.scheduled_start, service_appointments.scheduled_end, service_appointments.arrival_window_start, service_appointments.arrival_window_end, service_appointments.duration_minutes, service_appointments.address: optional scheduling windows
--   - assigned_resources.service_resource_id, assigned_resources.service_crew_id: assigned either to crew or individual resource
--   - travel_time_cache: standard lookup cache
--   - maintenance_plans.asset_id, maintenance_plans.work_type_id: optional template defaults
--   - case_milestones.completed_at: active milestone is not yet completed

-- 0. Core reference / CRM objects
CREATE TABLE accounts (
    id              BIGINT PRIMARY KEY,
    name            TEXT NOT NULL,
    billing_address JSONB,
    operating_hours_id BIGINT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE contacts (
    id              BIGINT PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES accounts(id),
    name            TEXT NOT NULL,
    email           TEXT,
    phone           TEXT
);
CREATE INDEX idx_contacts_account_id ON contacts (account_id);

CREATE TABLE app_users (
    id              BIGINT PRIMARY KEY,
    name            TEXT NOT NULL,
    email           TEXT UNIQUE NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cases (
    id              BIGINT PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES accounts(id),
    contact_id      BIGINT REFERENCES contacts(id),
    subject         TEXT NOT NULL,
    status          TEXT NOT NULL DEFAULT 'new',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_cases_account_status ON cases (account_id, status);

-- 1. Assets & pricing
CREATE TABLE products (
    id              BIGINT PRIMARY KEY,
    sku             TEXT UNIQUE NOT NULL,
    name            TEXT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE price_books (
    id              BIGINT PRIMARY KEY,
    name            TEXT NOT NULL,
    is_standard     BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE price_book_entries (
    id              BIGINT PRIMARY KEY,
    price_book_id   BIGINT NOT NULL REFERENCES price_books(id),
    product_id      BIGINT NOT NULL REFERENCES products(id),
    unit_price      NUMERIC(12,2) NOT NULL,
    currency_code   TEXT NOT NULL DEFAULT 'USD',
    UNIQUE (price_book_id, product_id)
);

CREATE TABLE assets (
    id              BIGINT PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES accounts(id),
    product_id      BIGINT REFERENCES products(id),
    name            TEXT NOT NULL,
    serial_number   TEXT,
    install_date    DATE,
    status          TEXT NOT NULL DEFAULT 'installed'
);
CREATE INDEX idx_assets_account_id ON assets (account_id);

CREATE TABLE asset_relationships (
    id              BIGINT PRIMARY KEY,
    asset_id        BIGINT NOT NULL REFERENCES assets(id),
    related_asset_id BIGINT NOT NULL REFERENCES assets(id),
    relationship_type TEXT NOT NULL,
    CONSTRAINT chk_asset_rel_not_self CHECK (asset_id != related_asset_id),
    UNIQUE (asset_id, related_asset_id, relationship_type)
);

-- 2. Operating hours, contracts & entitlements
CREATE TABLE operating_hours (
    id              BIGINT PRIMARY KEY,
    name            TEXT NOT NULL,
    timezone        TEXT NOT NULL DEFAULT 'UTC'
);

CREATE TABLE time_slots (
    id              BIGINT PRIMARY KEY,
    operating_hours_id BIGINT NOT NULL REFERENCES operating_hours(id) ON DELETE CASCADE,
    day_of_week     SMALLINT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    CONSTRAINT chk_time_slots_end_after_start CHECK (end_time > start_time)
);
CREATE INDEX idx_time_slots_hours_day ON time_slots (operating_hours_id, day_of_week);

ALTER TABLE accounts ADD CONSTRAINT fk_account_hours
    FOREIGN KEY (operating_hours_id) REFERENCES operating_hours(id);

CREATE TABLE service_contracts (
    id              BIGINT PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES accounts(id),
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          TEXT NOT NULL DEFAULT 'active',
    CONSTRAINT chk_contracts_dates CHECK (end_date > start_date)
);
CREATE INDEX idx_service_contracts_account_id ON service_contracts (account_id);

CREATE TABLE entitlements (
    id              BIGINT PRIMARY KEY,
    service_contract_id BIGINT REFERENCES service_contracts(id),
    account_id      BIGINT NOT NULL REFERENCES accounts(id),
    operating_hours_id BIGINT REFERENCES operating_hours(id),
    type            TEXT NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_entitlements_active ON entitlements (account_id) WHERE is_active;

-- 3. Work order hierarchy & skills
CREATE TABLE work_types (
    id              BIGINT PRIMARY KEY,
    name            TEXT NOT NULL,
    default_duration_minutes INT NOT NULL DEFAULT 60,
    estimated_travel_minutes INT NOT NULL DEFAULT 0
);

CREATE TABLE work_orders (
    id              BIGINT PRIMARY KEY,
    parent_work_order_id BIGINT REFERENCES work_orders(id),
    case_id         BIGINT REFERENCES cases(id),
    account_id      BIGINT NOT NULL REFERENCES accounts(id),
    entitlement_id  BIGINT REFERENCES entitlements(id),
    contact_id      BIGINT REFERENCES contacts(id),
    asset_id        BIGINT REFERENCES assets(id),
    work_type_id    BIGINT REFERENCES work_types(id),
    price_book_id   BIGINT REFERENCES price_books(id),
    status          TEXT NOT NULL DEFAULT 'new',
    priority        SMALLINT NOT NULL DEFAULT 3,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_work_orders_not_self REFERENCES work_orders(id) CHECK (parent_work_order_id != id)
);
CREATE INDEX idx_work_orders_account_status ON work_orders (account_id, status);
CREATE INDEX idx_work_orders_parent ON work_orders (parent_work_order_id);

CREATE TABLE work_order_line_items (
    id              BIGINT PRIMARY KEY,
    parent_line_item_id BIGINT REFERENCES work_order_line_items(id),
    work_order_id   BIGINT NOT NULL REFERENCES work_orders(id) ON DELETE CASCADE,
    asset_id        BIGINT REFERENCES assets(id),
    work_type_id    BIGINT REFERENCES work_types(id),
    price_book_entry_id BIGINT REFERENCES price_book_entries(id),
    status          TEXT NOT NULL DEFAULT 'new',
    CONSTRAINT chk_line_items_not_self CHECK (parent_line_item_id != id)
);
CREATE INDEX idx_line_items_work_order ON work_order_line_items (work_order_id);

CREATE TABLE skills (
    id              BIGINT PRIMARY KEY,
    name            TEXT UNIQUE NOT NULL,
    description     TEXT
);

CREATE TABLE skill_requirements (
    id              BIGINT PRIMARY KEY,
    skill_id        BIGINT NOT NULL REFERENCES skills(id),
    required_for_type TEXT NOT NULL CHECK (required_for_type IN ('work_type','work_order','work_order_line_item')),
    required_for_id BIGINT NOT NULL,
    min_skill_level SMALLINT NOT NULL DEFAULT 1
);
CREATE INDEX idx_skill_req_lookup ON skill_requirements (required_for_type, required_for_id);

-- 4. Territory & workforce
CREATE TABLE service_territories (
    id              BIGINT PRIMARY KEY,
    parent_territory_id BIGINT REFERENCES service_territories(id),
    operating_hours_id BIGINT REFERENCES operating_hours(id),
    name            TEXT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_territories_not_self CHECK (parent_territory_id != id)
);
CREATE INDEX idx_service_territories_parent ON service_territories (parent_territory_id);

CREATE TABLE service_crews (
    id              BIGINT PRIMARY KEY,
    name            TEXT NOT NULL
);

CREATE TABLE service_resources (
    id              BIGINT PRIMARY KEY,
    user_id         BIGINT REFERENCES app_users(id),
    service_crew_id BIGINT REFERENCES service_crews(id),
    name            TEXT NOT NULL,
    resource_type   TEXT NOT NULL DEFAULT 'technician',
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_service_resources_user ON service_resources (user_id);

CREATE TABLE service_crew_members (
    id              BIGINT PRIMARY KEY,
    service_crew_id BIGINT NOT NULL REFERENCES service_crews(id),
    service_resource_id BIGINT NOT NULL REFERENCES service_resources(id),
    member_role     TEXT NOT NULL DEFAULT 'member',
    start_date      DATE NOT NULL DEFAULT CURRENT_DATE,
    end_date        DATE,
    UNIQUE (service_crew_id, service_resource_id, start_date)
);

CREATE TABLE service_resource_skills (
    id              BIGINT PRIMARY KEY,
    service_resource_id BIGINT NOT NULL REFERENCES service_resources(id),
    skill_id        BIGINT NOT NULL REFERENCES skills(id),
    skill_level     SMALLINT NOT NULL DEFAULT 1,
    valid_from      DATE NOT NULL DEFAULT CURRENT_DATE,
    valid_to        DATE,
    UNIQUE (service_resource_id, skill_id)
);
CREATE INDEX idx_resource_skills_matching ON service_resource_skills (skill_id, skill_level);

CREATE TABLE service_territory_members (
    id              BIGINT PRIMARY KEY,
    service_territory_id BIGINT NOT NULL REFERENCES service_territories(id),
    service_resource_id BIGINT NOT NULL REFERENCES service_resources(id),
    operating_hours_id BIGINT REFERENCES operating_hours(id),
    territory_type  TEXT NOT NULL DEFAULT 'primary',
    travel_mode     TEXT NOT NULL DEFAULT 'driving',
    UNIQUE (service_territory_id, service_resource_id)
);
CREATE INDEX idx_territory_members_resource ON service_territory_members (service_resource_id);

CREATE TABLE shifts (
    id              BIGINT PRIMARY KEY,
    service_resource_id BIGINT NOT NULL REFERENCES service_resources(id),
    service_territory_id BIGINT NOT NULL REFERENCES service_territories(id),
    start_time      TIMESTAMPTZ NOT NULL,
    end_time        TIMESTAMPTZ NOT NULL,
    shift_type      TEXT NOT NULL DEFAULT 'standard',
    CONSTRAINT chk_shifts_end_after_start CHECK (end_time > start_time)
);
CREATE INDEX idx_shifts_lookup ON shifts (service_resource_id, start_time);

CREATE TABLE service_resource_capacities (
    id              BIGINT PRIMARY KEY,
    service_resource_id BIGINT NOT NULL REFERENCES service_resources(id),
    capacity_type   TEXT NOT NULL,
    capacity_value  NUMERIC(8,2) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE
);
CREATE INDEX idx_capacities_lookup ON service_resource_capacities (service_resource_id);

CREATE TABLE resource_absences (
    id              BIGINT PRIMARY KEY,
    service_resource_id BIGINT NOT NULL REFERENCES service_resources(id),
    absence_type    TEXT NOT NULL,
    start_time      TIMESTAMPTZ NOT NULL,
    end_time        TIMESTAMPTZ NOT NULL,
    status          TEXT NOT NULL DEFAULT 'approved',
    CONSTRAINT chk_absences_end_after_start CHECK (end_time > start_time)
);
CREATE INDEX idx_absences_lookup ON resource_absences (service_resource_id, start_time, end_time);

CREATE TABLE resource_preferences (
    id              BIGINT PRIMARY KEY,
    service_resource_id BIGINT NOT NULL REFERENCES service_resources(id),
    related_record_type TEXT NOT NULL CHECK (related_record_type IN ('work_order','account')),
    related_record_id BIGINT NOT NULL,
    preference_type TEXT NOT NULL CHECK (preference_type IN ('preferred','excluded'))
);
CREATE INDEX idx_resource_preferences_lookup ON resource_preferences (related_record_type, related_record_id);

-- 5. Scheduling core
CREATE TABLE service_appointments (
    id              BIGINT NOT NULL,
    parent_record_type TEXT NOT NULL CHECK (parent_record_type IN
                        ('work_order','work_order_line_item','account','asset','lead','opportunity','case')),
    parent_record_id BIGINT NOT NULL,
    account_id      BIGINT REFERENCES accounts(id),
    contact_id      BIGINT REFERENCES contacts(id),
    service_territory_id BIGINT REFERENCES service_territories(id),
    work_type_id    BIGINT REFERENCES work_types(id),
    status          TEXT NOT NULL DEFAULT 'none',
    scheduled_start TIMESTAMPTZ,
    scheduled_end   TIMESTAMPTZ,
    arrival_window_start TIMESTAMPTZ,
    arrival_window_end TIMESTAMPTZ,
    duration_minutes INT,
    address         JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (created_at, id)
) PARTITION BY RANGE (created_at);

CREATE TABLE service_appointments_2026_07 PARTITION OF service_appointments
    FOR VALUES FROM ('2026-07-01') TO ('2026-08-01');

CREATE INDEX idx_appointments_parent ON service_appointments (parent_record_type, parent_record_id);
CREATE INDEX idx_appointments_territory_start ON service_appointments (service_territory_id, scheduled_start);
CREATE INDEX idx_appointments_status_start ON service_appointments (status, scheduled_start);

CREATE TABLE assigned_resources (
    id              BIGINT PRIMARY KEY,
    service_appointment_id BIGINT NOT NULL,
    service_appointment_created_at TIMESTAMPTZ NOT NULL,
    service_resource_id BIGINT REFERENCES service_resources(id),
    service_crew_id BIGINT REFERENCES service_crews(id),
    is_primary_resource BOOLEAN NOT NULL DEFAULT TRUE,
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    status          TEXT NOT NULL DEFAULT 'assigned',
    CONSTRAINT chk_assigned_resource_or_crew CHECK (service_resource_id IS NOT NULL OR service_crew_id IS NOT NULL)
);
CREATE INDEX idx_assigned_resources_appointment ON assigned_resources (service_appointment_id);
CREATE INDEX idx_assigned_resources_resource ON assigned_resources (service_resource_id, assigned_at);

-- 6. Status transition state machines
CREATE TABLE status_transitions (
    entity_type     TEXT NOT NULL,
    from_status     TEXT NOT NULL,
    to_status       TEXT NOT NULL,
    PRIMARY KEY (entity_type, from_status, to_status)
);

INSERT INTO status_transitions (entity_type, from_status, to_status) VALUES
    ('service_appointment','none','scheduled'),
    ('service_appointment','scheduled','dispatched'),
    ('service_appointment','dispatched','in_progress'),
    ('service_appointment','in_progress','completed'),
    ('service_appointment','scheduled','canceled'),
    ('service_appointment','dispatched','canceled'),
    ('service_appointment','in_progress','canceled');

-- 7. Travel time cache
CREATE TABLE travel_time_cache (
    origin_territory_id BIGINT NOT NULL REFERENCES service_territories(id),
    dest_lat_bucket SMALLINT NOT NULL,
    dest_lng_bucket SMALLINT NOT NULL,
    estimated_minutes INT NOT NULL,
    computed_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (origin_territory_id, dest_lat_bucket, dest_lng_bucket)
);

-- 8. Maintenance Plans
CREATE TABLE maintenance_plans (
    id              BIGINT PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES accounts(id),
    asset_id        BIGINT REFERENCES assets(id),
    work_type_id    BIGINT REFERENCES work_types(id),
    frequency       TEXT NOT NULL,
    generation_lead_days INT NOT NULL DEFAULT 14,
    next_generation_date DATE NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE maintenance_assets (
    maintenance_plan_id BIGINT NOT NULL REFERENCES maintenance_plans(id),
    asset_id        BIGINT NOT NULL REFERENCES assets(id),
    PRIMARY KEY (maintenance_plan_id, asset_id)
);

-- 9. Milestones & SLAs
CREATE TABLE entitlement_processes (
    id              BIGINT PRIMARY KEY,
    entitlement_id  BIGINT NOT NULL REFERENCES entitlements(id),
    name            TEXT NOT NULL
);

CREATE TABLE milestones (
    id              BIGINT PRIMARY KEY,
    entitlement_process_id BIGINT NOT NULL REFERENCES entitlement_processes(id),
    name            TEXT NOT NULL,
    target_minutes  INT NOT NULL,
    sequence        SMALLINT NOT NULL
);

CREATE TABLE case_milestones (
    id              BIGINT PRIMARY KEY,
    case_id         BIGINT NOT NULL REFERENCES cases(id),
    milestone_id    BIGINT NOT NULL REFERENCES milestones(id),
    started_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at    TIMESTAMPTZ,
    is_breached     BOOLEAN NOT NULL DEFAULT FALSE
);

-- Triggers for validation
CREATE OR REPLACE FUNCTION validate_service_appointment_parent()
RETURNS TRIGGER AS $$
BEGIN
    CASE NEW.parent_record_type
        WHEN 'work_order' THEN
            IF NOT EXISTS (SELECT 1 FROM work_orders WHERE id = NEW.parent_record_id) THEN
                RAISE EXCEPTION 'work_order % does not exist', NEW.parent_record_id;
            END IF;
        WHEN 'work_order_line_item' THEN
            IF NOT EXISTS (SELECT 1 FROM work_order_line_items WHERE id = NEW.parent_record_id) THEN
                RAISE EXCEPTION 'work_order_line_item % does not exist', NEW.parent_record_id;
            END IF;
        WHEN 'account' THEN
            IF NOT EXISTS (SELECT 1 FROM accounts WHERE id = NEW.parent_record_id) THEN
                RAISE EXCEPTION 'account % does not exist', NEW.parent_record_id;
            END IF;
        WHEN 'case' THEN
            IF NOT EXISTS (SELECT 1 FROM cases WHERE id = NEW.parent_record_id) THEN
                RAISE EXCEPTION 'case % does not exist', NEW.parent_record_id;
            END IF;
        WHEN 'asset' THEN
            IF NOT EXISTS (SELECT 1 FROM assets WHERE id = NEW.parent_record_id) THEN
                RAISE EXCEPTION 'asset % does not exist', NEW.parent_record_id;
            END IF;
        ELSE
            RAISE EXCEPTION 'unhandled parent_record_type %', NEW.parent_record_type;
    END CASE;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_sa_parent
    BEFORE INSERT OR UPDATE ON service_appointments
    FOR EACH ROW EXECUTE FUNCTION validate_service_appointment_parent();

CREATE OR REPLACE FUNCTION enforce_status_transition()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = OLD.status THEN RETURN NEW; END IF;
    IF NOT EXISTS (
        SELECT 1 FROM status_transitions
        WHERE entity_type = TG_ARGV[0] AND from_status = OLD.status AND to_status = NEW.status
    ) THEN
        RAISE EXCEPTION 'invalid transition % -> % for %', OLD.status, NEW.status, TG_ARGV[0];
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sa_status
    BEFORE UPDATE OF status ON service_appointments
    FOR EACH ROW EXECUTE FUNCTION enforce_status_transition('service_appointment');
