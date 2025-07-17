-- =====================================================
-- Book Service Database Setup Script
-- PostgreSQL Database and User Creation
-- =====================================================

-- This script should be run as a PostgreSQL superuser (e.g., postgres)
-- to create the database and user for the book-service

-- =====================================================
-- 1. CREATE DATABASE
-- =====================================================

-- Drop database if exists (for development only)
-- DROP DATABASE IF EXISTS book_service;

-- Create database
CREATE DATABASE book_service
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    TEMPLATE = template0;

-- Add comment
COMMENT ON DATABASE book_service IS 'Database for the Book Service microservice in the Library Management System';

-- =====================================================
-- 2. CREATE USER AND GRANT PERMISSIONS
-- =====================================================

-- Create user for book service
CREATE USER book_service_user WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    INHERIT
    NOREPLICATION
    CONNECTION LIMIT -1
    PASSWORD 'book_service_password_2024';

-- Grant permissions on database
GRANT CONNECT ON DATABASE book_service TO book_service_user;
GRANT USAGE ON SCHEMA public TO book_service_user;
GRANT CREATE ON SCHEMA public TO book_service_user;

-- Grant permissions on all tables (current and future)
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO book_service_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO book_service_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO book_service_user;

-- Grant permissions on future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO book_service_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO book_service_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO book_service_user;

-- =====================================================
-- 3. CONNECT TO DATABASE AND SETUP EXTENSIONS
-- =====================================================

-- Connect to the book_service database
\c book_service;

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";  -- For similarity searches
CREATE EXTENSION IF NOT EXISTS "unaccent"; -- For accent-insensitive searches

-- =====================================================
-- 4. VERIFICATION
-- =====================================================

-- Verify database creation
SELECT 
    datname as database_name,
    pg_encoding_to_char(encoding) as encoding,
    datcollate as collate,
    datctype as ctype
FROM pg_database 
WHERE datname = 'book_service';

-- Verify user creation
SELECT 
    usename as username,
    usesuper as is_superuser,
    usecreatedb as can_create_db,
    usecreaterole as can_create_role
FROM pg_user 
WHERE usename = 'book_service_user';

-- Verify extensions
SELECT 
    extname as extension_name,
    extversion as version
FROM pg_extension 
WHERE extname IN ('uuid-ossp', 'pg_trgm', 'unaccent');

-- =====================================================
-- 5. SAMPLE CONNECTION TEST
-- =====================================================

-- Test connection with the new user (run this separately)
/*
psql -h localhost -U book_service_user -d book_service -c "SELECT current_database(), current_user, version();"
*/

-- =====================================================
-- 6. DEVELOPMENT CONFIGURATION
-- =====================================================

-- For development environment, you might want to adjust these settings
-- (These should be set in postgresql.conf for production)

-- Show current settings
SELECT name, setting, unit, context 
FROM pg_settings 
WHERE name IN (
    'max_connections',
    'shared_buffers',
    'effective_cache_size',
    'maintenance_work_mem',
    'checkpoint_completion_target',
    'wal_buffers',
    'default_statistics_target'
);

-- =====================================================
-- 7. CLEANUP COMMANDS (FOR DEVELOPMENT)
-- =====================================================

-- Uncomment these commands if you need to clean up during development

/*
-- Drop database (will fail if there are active connections)
DROP DATABASE IF EXISTS book_service;

-- Drop user
DROP USER IF EXISTS book_service_user;

-- Kill active connections to database (if needed)
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'book_service' AND pid <> pg_backend_pid();
*/