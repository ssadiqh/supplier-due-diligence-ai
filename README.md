# Australian Supplier Onboarding and Counterparty Due-Diligence Assistant

A learning project demonstrating enterprise-grade AI architecture through a realistic Australian supplier-onboarding workflow.

## Quick start

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker Desktop
- Git

### Local setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/supplier-due-diligence-ai.git
cd supplier-due-diligence-ai
```

2. Copy environment template:
```bash
cp .env.example .env
# Edit .env with your local settings
```

3. Start local services (PostgreSQL + pgvector):
```bash
docker compose up -d
```

4. Build the backend:
```bash
cd backend
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

The API will start at `http://localhost:8080`.

## Project structure

- **backend/** - Spring Boot application, agents, rules, RAG
- **sample-data/** - Synthetic supplier documents, policies, evaluation baselines
- **docs/** - Architecture decisions, learning record

## Architecture overview

See [docs/LEARNING.md](docs/LEARNING.md) for design decisions, scope, and build order.

### Key principles
- **Bounded agents**: Specialist AI components with typed input/output
- **Deterministic rules**: Business logic outside the model
- **Human authority**: Authorised reviewers make final decisions
- **Evidence grounding**: All findings cite documents, tools, policies or rules
- **Controlled tools**: Deny-by-default, schema-validated, case-scoped

## Development

Each vertical slice adds one capability without changing the core case model:

1. Case API, document upload, structured state
2. Deterministic workflow and rules
3. Document Evidence Agent
4. ABN Lookup and Supplier Master tools
5. MCP server and governance layer
6. Policy RAG and retrieval
7. Entity Resolution Agent
8. Policy and Risk Agent
9. Review Synthesis Agent
10. Asynchronous events and human review

## Status

**Release 1**: Learning implementation in progress.

## License

Private learning project.
