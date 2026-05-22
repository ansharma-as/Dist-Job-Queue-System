# Distributed Job Queue System

This project is a high-performance, resilient, and observable distributed job queue system built to handle asynchronous workloads. It mirrors the architectural patterns used by systems like Celery, Sidekiq, and AWS SQS.

## Architecture

* **Backend**: Java 21, Spring Boot 3.2.x
* **Message Broker**: Apache Kafka (partitioned by job type/tenant)
* **Database**: PostgreSQL (maintaining job state machine and optimistic locking)
* **Frontend**: Next.js (React) + Tailwind CSS

## Microservices

To keep the architecture manageable while demonstrating core distributed concepts, the system is condensed into three primary services:

1. **`job-api-service` (REST API)**: The high-availability I/O bound service. It accepts job payloads, validates them, and persists them to the database in a `PENDING` state. It also serves as the data layer for the frontend dashboard to check queue depth and job statuses.
2. **`job-scheduler-service` (The Orchestrator)**: The database-bound polling service. It safely locks `PENDING` jobs in the database and publishes them to Kafka. It also sweeps the database for `FAILED` jobs to handle exponential backoff retries, and moves exhausted jobs to `DEAD_LETTERED`.
3. **`worker-service` (The Muscle)**: The CPU-bound execution service. It consumes messages from Kafka, runs the heavy background processing, and updates the state to `COMPLETED` or `FAILED`. This is scaled horizontally via Kafka consumer groups.

## Getting Started

### 1. Start Infrastructure
```bash
docker-compose up -d
```
Starts Kafka, Zookeeper, PostgreSQL, Prometheus, and Grafana.

### 2. Run Services
Compile and run the Spring Boot services:
```bash
mvn clean install
# Run each service via IDE or mvn spring-boot:run
```

### 3. Run Dashboard
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:3000` to view the real-time monitoring dashboard.

## Job State Machine
`PENDING` → `QUEUED` → `PROCESSING` → `COMPLETED`
                                  ↘ `FAILED` → (Retry) → `PROCESSING`
                                             ↘ (Exhausted) → `DEAD_LETTERED`
