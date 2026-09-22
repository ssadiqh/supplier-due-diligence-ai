CREATE TABLE IF NOT EXISTS cases (
    id UUID PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
    supplier_abn VARCHAR(11),
    supplier_legal_name VARCHAR(255),
    requested_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_cases_status ON cases(status);
