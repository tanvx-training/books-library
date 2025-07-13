-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create audit_logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    service_name VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    action_type VARCHAR(20) NOT NULL,
    user_id VARCHAR(36),
    user_info JSONB,
    old_value JSONB,
    new_value JSONB,
    changes JSONB,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    request_id VARCHAR(100)
);

-- Create indexes for optimized queries
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_service ON audit_logs(service_name); 