# Kafka Topic Design

## Topics

1. **`job-pending`**
   - **Purpose**: Jobs that are validated and ready to be picked up by workers.
   - **Producers**: `job-scheduler-service`
   - **Consumers**: `worker-service`
   - **Partitioning Strategy**: Partitioned by job `type` or `tenant_id` to ensure ordered execution within a specific domain, allowing parallel execution across different domains.
   - **Replication Factor**: 3 (in production).

2. **`job-events`**
   - **Purpose**: Event sourcing and monitoring for state changes (e.g., QUEUED -> PROCESSING -> COMPLETED).
   - **Producers**: All services that change state (`job-submission-service`, `worker-service`, `retry-service`).
   - **Consumers**: `monitoring-service` (for real-time analytics), `dlq-service` (for tracking failures).
   - **Partitioning Strategy**: Partitioned by `job_id`.

3. **`job-dlq`**
   - **Purpose**: Poison pills or messages that cannot be deserialized or processed at all by Kafka.
   - **Producers**: `worker-service` (on permanent serialization/deserialization errors).
   - **Consumers**: `dlq-service`.
   - **Partitioning Strategy**: Can be a single partition if low volume.

## Consumer Groups
- **`worker-group`**: Horizontally scaled group of `worker-service` instances consuming `job-pending`. Kafka automatically handles partition rebalancing if a worker dies.
- **`monitoring-group`**: Group of `monitoring-service` instances tracking real-time metrics.
