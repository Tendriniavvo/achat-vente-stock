CREATE SCHEMA IF NOT EXISTS raw;
CREATE SCHEMA IF NOT EXISTS staging;
CREATE SCHEMA IF NOT EXISTS marts;
CREATE SCHEMA IF NOT EXISTS audit;

CREATE TABLE IF NOT EXISTS audit.pipeline_runs (
    id BIGSERIAL PRIMARY KEY,
    dag_id VARCHAR(255),
    task_id VARCHAR(255),
    run_id VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    rows_processed BIGINT DEFAULT 0,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit.data_quality_results (
    id BIGSERIAL PRIMARY KEY,
    run_id VARCHAR(255),
    check_name VARCHAR(255) NOT NULL,
    table_name VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    severity VARCHAR(50) DEFAULT 'info',
    failed_rows BIGINT DEFAULT 0,
    details TEXT,
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit.email_logs (
    id BIGSERIAL PRIMARY KEY,
    run_id VARCHAR(255),
    email_type VARCHAR(100) NOT NULL,
    recipients TEXT NOT NULL,
    subject TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit.alerts (
    id BIGSERIAL PRIMARY KEY,
    run_id VARCHAR(255),
    alert_type VARCHAR(100) NOT NULL,
    severity VARCHAR(50) NOT NULL DEFAULT 'warning',
    business_key VARCHAR(255),
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);
