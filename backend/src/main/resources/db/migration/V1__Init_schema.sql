CREATE TABLE IF NOT EXISTS cases (
    id UUID PRIMARY KEY,
    case_id VARCHAR(255) UNIQUE NOT NULL,
    supplier_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
    supplier_abn VARCHAR(11),
    supplier_legal_name VARCHAR(255),
    requested_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_cases_case_id ON cases(case_id);
CREATE INDEX idx_cases_status ON cases(status);
