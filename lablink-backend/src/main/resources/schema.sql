-- ============================================================
-- LabLink Database Schema
-- Target: Supabase (PostgreSQL 15)
--
-- HOW TO RUN:
--   Supabase Dashboard → SQL Editor → paste and run.
--   Safe to re-run: all statements use IF NOT EXISTS / OR REPLACE.
--
-- NOTE: ddl-auto=none means Spring Boot NEVER touches this schema.
--   All changes must be made here and re-run in the SQL editor.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
-- ENUM TYPES
-- Use DO block to safely create enums only if they don't exist.
-- ============================================================

DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('STUDENT', 'ADMIN');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    CREATE TYPE equipment_status AS ENUM ('AVAILABLE', 'UNAVAILABLE', 'MAINTENANCE');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

DO $$ BEGIN
    CREATE TYPE borrow_status AS ENUM ('ACTIVE', 'RETURNED', 'OVERDUE');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- ============================================================
-- TABLE: users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    id_number     VARCHAR(20)  UNIQUE,           -- CIT student ID: XX-XXXX-XXX
    role          user_role    NOT NULL DEFAULT 'STUDENT',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_id_number ON users(id_number);

-- ============================================================
-- TABLE: categories
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- ============================================================
-- TABLE: equipment
-- ============================================================
CREATE TABLE IF NOT EXISTS equipment (
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

CREATE INDEX IF NOT EXISTS idx_equipment_status      ON equipment(status);
CREATE INDEX IF NOT EXISTS idx_equipment_category_id ON equipment(category_id);
CREATE INDEX IF NOT EXISTS idx_equipment_name_search ON equipment USING gin(to_tsvector('english', name));

-- Auto-update updated_at on any equipment row change
CREATE OR REPLACE FUNCTION update_equipment_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_equipment_updated_at ON equipment;
CREATE TRIGGER trg_equipment_updated_at
    BEFORE UPDATE ON equipment
    FOR EACH ROW EXECUTE FUNCTION update_equipment_timestamp();

-- ============================================================
-- TABLE: borrow_records
-- ============================================================
CREATE TABLE IF NOT EXISTS borrow_records (
    id                   UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id              UUID          NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    equipment_id         UUID          NOT NULL REFERENCES equipment(id) ON DELETE RESTRICT,
    borrow_date          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    expected_return_date DATE          NOT NULL,
    actual_return_date   TIMESTAMPTZ,
    status               borrow_status NOT NULL DEFAULT 'ACTIVE',
    purpose              TEXT,
    remarks              TEXT
);

CREATE INDEX IF NOT EXISTS idx_borrow_user_id      ON borrow_records(user_id);
CREATE INDEX IF NOT EXISTS idx_borrow_equipment_id ON borrow_records(equipment_id);
CREATE INDEX IF NOT EXISTS idx_borrow_status       ON borrow_records(status);

-- ============================================================
-- TABLE: refresh_tokens (for future server-side revocation)
-- ============================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- SEED DATA — Categories
-- Only inserts if the categories table is empty.
-- ============================================================
INSERT INTO categories (name, description)
SELECT * FROM (VALUES
    ('Microcontrollers', 'Arduino, Raspberry Pi, ESP32, and similar programmable boards'),
    ('Sensors',          'Temperature, humidity, motion, ultrasonic, and other sensor modules'),
    ('Optics',           'Lenses, laser modules, fiber optics, and optical measurement tools'),
    ('Test Equipment',   'Multimeters, oscilloscopes, power supplies, and signal generators')
) AS v(name, description)
WHERE NOT EXISTS (SELECT 1 FROM categories LIMIT 1);
