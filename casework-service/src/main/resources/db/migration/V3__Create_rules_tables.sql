-- Rules table
CREATE TABLE rules (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rules_is_active ON rules(is_active);
CREATE INDEX idx_rules_type ON rules(rule_type);

-- Rule Results table (tracks evaluations)
CREATE TABLE rule_results (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    rule_id UUID NOT NULL REFERENCES rules(id),
    passed BOOLEAN NOT NULL,
    details TEXT,
    evidence TEXT,
    evaluated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rule_results_case_id ON rule_results(case_id);
CREATE INDEX idx_rule_results_rule_id ON rule_results(rule_id);
CREATE INDEX idx_rule_results_passed ON rule_results(passed);
