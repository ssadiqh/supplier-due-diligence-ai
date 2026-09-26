-- Extraction Results table for tracking LLM-based document evidence extraction
CREATE TABLE extraction_results (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    tool_name VARCHAR(100) NOT NULL,                -- "DOCUMENT_EXTRACTION"
    tool_type VARCHAR(100) NOT NULL,                -- "EVIDENCE_EXTRACTION"
    input TEXT,                                     -- JSON: {documentId, documentName, chunkCount}
    output TEXT,                                    -- JSON: {facts[], summary, complete}
    success BOOLEAN NOT NULL,                       -- true = extraction succeeded, false = failed
    error_message TEXT,                             -- Error details if failed
    evidence TEXT,                                  -- Human-readable findings with confidence
    prompt_version VARCHAR(20),                     -- "v1", "v2", "v3" for A/B testing
    model_used VARCHAR(100),                        -- "gpt-4-turbo", "claude-3-opus", etc.
    tokens_used INTEGER,                            -- Token count for cost tracking
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indices for common queries
CREATE INDEX idx_extraction_results_case_id ON extraction_results(case_id);
CREATE INDEX idx_extraction_results_document_id ON extraction_results(document_id);
CREATE INDEX idx_extraction_results_success ON extraction_results(success);
CREATE INDEX idx_extraction_results_prompt_version ON extraction_results(prompt_version);
CREATE INDEX idx_extraction_results_executed_at ON extraction_results(executed_at);
