-- Migration 0009: Add DB level boundary constraints & identity sequence auto-increment

CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 INCREMENT BY 1;

ALTER TABLE users 
    ALTER COLUMN id SET DEFAULT nextval('users_id_seq'),
    ALTER COLUMN username SET NOT NULL,
    ALTER COLUMN password SET NOT NULL,
    ALTER COLUMN email SET NOT NULL;

ALTER TABLE users DROP CONSTRAINT IF EXISTS users_username_key;

ALTER SEQUENCE users_id_seq OWNED BY users.id;

-- Add DB boundary constraints for email format and username length
ALTER TABLE users ADD CONSTRAINT chk_users_email_format 
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE users ADD CONSTRAINT chk_users_username_length 
    CHECK (char_length(username) >= 3 AND char_length(username) <= 50);

-- Enforce DB level strict uniqueness for email
ALTER TABLE users ADD CONSTRAINT uq_users_email UNIQUE (email);

-- Create fast lookup index for RBAC role assignments and email authentication
CREATE INDEX IF NOT EXISTS idx_users_email_lower ON users (LOWER(email));

