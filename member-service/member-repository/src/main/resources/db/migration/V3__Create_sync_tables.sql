-- Create sync_states table
CREATE TABLE sync_states (
    id BIGSERIAL PRIMARY KEY,
    sync_type VARCHAR(50) NOT NULL,
    last_sync_time TIMESTAMP NOT NULL,
    synced_user_count INTEGER DEFAULT 0,
    success_count INTEGER DEFAULT 0,
    failure_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for performance
CREATE INDEX idx_sync_states_sync_type ON sync_states(sync_type);
CREATE INDEX idx_sync_states_created_at ON sync_states(created_at);

-- Create sync_audit_logs table for detailed tracking
CREATE TABLE sync_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    keycloak_id VARCHAR(255) NOT NULL,
    operation VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE
    status VARCHAR(20) NOT NULL, -- SUCCESS, FAILURE
    error_message TEXT,
    sync_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for sync_audit_logs
CREATE INDEX idx_sync_audit_logs_keycloak_id ON sync_audit_logs(keycloak_id);
CREATE INDEX idx_sync_audit_logs_sync_time ON sync_audit_logs(sync_time);
CREATE INDEX idx_sync_audit_logs_status ON sync_audit_logs(status);