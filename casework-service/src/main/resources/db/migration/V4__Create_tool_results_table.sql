-- Tool Results table for tracking tool executions
CREATE TABLE tool_results (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    tool_name VARCHAR(100) NOT NULL,                -- "ABN_LOOKUP"
    tool_type VARCHAR(100) NOT NULL,                -- "SUPPLIER_VERIFICATION"
    input TEXT,                                     -- JSON input to tool
    output TEXT,                                    -- JSON output from tool
    success BOOLEAN NOT NULL,                       -- true = success, false = failed
    error_message TEXT,                             -- Error details if failed
    evidence TEXT,                                  -- Human-readable findings
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indices for common queries
CREATE INDEX idx_tool_results_case_id ON tool_results(case_id);
CREATE INDEX idx_tool_results_tool_name ON tool_results(tool_name);
CREATE INDEX idx_tool_results_success ON tool_results(success);
CREATE INDEX idx_tool_results_executed_at ON tool_results(executed_at);
