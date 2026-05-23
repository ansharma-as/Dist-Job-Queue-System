# Distributed Job Queue System

I built this project to explore how large-scale background job processing works. It is a distributed system designed to handle asynchronous tasks reliably. I wanted to move away from simple in-memory queues and build something that can actually scale horizontally, tolerate failures, and guarantee message delivery.

## Tech Stack
- Java 21 & Spring Boot 3.2
- PostgreSQL (Database and state management)
- Apache Kafka (Message broker for decoupling services)
- Next.js & Tailwind CSS (Real-time monitoring dashboard)
- Docker & Docker Compose (Infrastructure)

## How it works

The system is broken down into three main microservices:

1. API Service (Port 8081): Receives incoming job requests via REST, validates them, and writes them to the database in a PENDING state. It also serves data to the frontend dashboard.
2. Scheduler Service (Port 8082): Runs in the background and polls the database for PENDING jobs. It safely locks them, publishes the job ID to Kafka, and updates the database state to QUEUED. It also handles retrying FAILED jobs.
3. Worker Service (Port 8083+): Listens to Kafka. When it receives a message, it executes the heavy lifting, simulates the processing time, and marks the job as COMPLETED or FAILED in the database.

## Running the project locally

First, you need to spin up the infrastructure (Postgres, Kafka, Zookeeper, Prometheus, Grafana).

```bash
docker-compose up -d
```

Next, open three separate terminal windows to run the Spring Boot microservices:

```bash
# Terminal 1
mvn spring-boot:run -pl job-api-service

# Terminal 2
mvn spring-boot:run -pl job-scheduler-service

# Terminal 3
mvn spring-boot:run -pl worker-service
```

Finally, start the frontend dashboard:

```bash
cd frontend
npm install
npm run dev
```

You can view the real-time monitoring dashboard at http://localhost:3000.

## Testing Scalability

To see the system scale, you can simulate a heavy load using Apache Benchmark (`ab`). I included a `payload.json` file in the root directory for this purpose.

```bash
ab -p payload.json -T application/json -c 50 -n 1000 http://localhost:8081/api/jobs
```

This will send 1000 jobs to the API concurrently. You can watch the queue depth spike on the frontend dashboard. 

To process them faster, open a new terminal and spin up a second worker on a different port:
```bash
mvn spring-boot:run -pl worker-service -Dspring-boot.run.arguments="--server.port=8084"
```

Kafka will automatically rebalance the consumer group, and you will see both workers pulling from the queue simultaneously.

## Job Lifecycle

A job generally moves through these states:
`PENDING` -> `QUEUED` -> `PROCESSING` -> `COMPLETED`

If a job fails, it goes to `FAILED`. The scheduler will pick it up and retry it up to a maximum number of attempts before moving it to `DEAD_LETTERED`.
