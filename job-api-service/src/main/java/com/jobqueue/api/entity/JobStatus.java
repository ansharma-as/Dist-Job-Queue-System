package com.jobqueue.api.entity;

public enum JobStatus {
    PENDING, QUEUED, PROCESSING, COMPLETED, FAILED, DEAD_LETTERED
}
