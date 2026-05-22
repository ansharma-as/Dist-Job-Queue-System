package com.jobqueue.worker.entity;

public enum JobStatus {
    PENDING, QUEUED, PROCESSING, COMPLETED, FAILED, DEAD_LETTERED
}
