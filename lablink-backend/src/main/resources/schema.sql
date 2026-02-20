-- ============================================================
-- LabLink Database Schema
-- Target: Supabase (PostgreSQL 15)
--
-- HOW TO RUN:
--   Supabase Dashboard → SQL Editor → paste and run this script.
--   Run it once on a fresh project. Safe to re-run (IF NOT EXISTS guards).
--
-- NOTE: pgcrypto and uuid-ossp are pre-installed on Supabase.
--   gen_random_uuid() is available natively in PostgreSQL 13+.
-- ============================================================

-- Extensions (Supabase has these pre-installed; harmless to re-declare)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- ENUM TYPES
-- ============================================================

CREATE TYPE user_role AS ENUM ('STUDENT', 'ADMIN');
CREATE TYPE equipment_status AS ENUM ('AVAILABLE', 'UNAVAILABLE', 'MAINTENANCE');
CREATE TYPE borrow_status AS ENUM ('ACTIVE', 'RETURNED', 'OVERDUE');

-- ============================================================
-- TABLE: users
-- ============================================================
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    role          user_role    NOT NULL DEFAULT 'STUDENT',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- ============================================================
-- TABLE: categories
-- ============================================================
CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Seed data
INSERT INTO categories (name, description) VALUES
    ('Microcontrollers', 'Arduino, ESP32, Raspberry Pi boards'),
    ('Sensors',          'Temperature, humidity, motion, ultrasonic'),
    ('Optics',           'Cameras, lenses, laser modules'),
    ('Test Tools',       'Multimeters, oscilloscopes, logic analyzers');

-- ============================================================
-- TABLE: equipment
-- ============================================================
CREATE TABLE equipment (
    id            UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id   UUID             NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    name          VARCHAR(255)     NOT NULL,
    description   TEXT,
    serial_number VARCHAR(100)     UNIQUE,
    status        equipment_status NOT NULL DEFAULT 'AVAILABLE',
    image_url     VARCHAR(500),
    created_at    TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_equipment_status      ON equipment(status);
CREATE INDEX idx_equipment_category_id ON equipment(category_id);
-- Full-text search index on name + description
CREATE INDEX idx_equipment_fts ON equipment USING GIN (
    to_tsvector('english', name || ' ' || COALESCE(description, ''))
);

-- Auto-update updated_at on row changes
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN NEW.updated_at = NOW(); RETURN NEW; END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_equipment_updated_at
    BEFORE UPDATE ON equipment
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

-- ============================================================
-- TABLE: borrow_records
-- ============================================================
CREATE TABLE borrow_records (
    id                   UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id              UUID          NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    equipment_id         UUID          NOT NULL REFERENCES equipment(id) ON DELETE RESTRICT,
    borrow_date          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    expected_return_date DATE          NOT NULL,
    actual_return_date   TIMESTAMPTZ,
    status               borrow_status NOT NULL DEFAULT 'ACTIVE',
    purpose              TEXT,
    remarks              TEXT,

    -- Business rule: one equipment item can only have one ACTIVE record at a time
    CONSTRAINT uq_equipment_active_borrow
        EXCLUDE USING btree (equipment_id WITH =)
        WHERE (status = 'ACTIVE')
);

CREATE INDEX idx_borrow_user_id      ON borrow_records(user_id);
CREATE INDEX idx_borrow_equipment_id ON borrow_records(equipment_id);
CREATE INDEX idx_borrow_status       ON borrow_records(status);

-- ============================================================
-- TABLE: refresh_tokens
-- ============================================================
CREATE TABLE refresh_tokens (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       TEXT        NOT NULL UNIQUE,
    expiry_date TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token   ON refresh_tokens(token);

-- ============================================================
-- TABLE: notifications
-- ============================================================
CREATE TABLE notifications (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message    TEXT        NOT NULL,
    is_read    BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(user_id, is_read);
