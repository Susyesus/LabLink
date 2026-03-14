-- LabLink — Migration: Add profile photo storage to users table
-- Run this in Supabase Dashboard → SQL Editor

-- photo_data: raw image bytes stored as PostgreSQL bytea (BLOB equivalent)
-- photo_type: MIME type ("image/jpeg" or "image/png") for correct Content-Type on retrieval
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS photo_data bytea,
    ADD COLUMN IF NOT EXISTS photo_type varchar(10);

-- Verify columns were added
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'users'
  AND column_name IN ('photo_data', 'photo_type');
