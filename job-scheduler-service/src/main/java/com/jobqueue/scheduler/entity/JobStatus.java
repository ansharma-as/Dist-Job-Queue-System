package com.jobqueue.scheduler.entity;

public enum JobStatus {
    PENDING, QUEUED, PROCESSING, COMPLETED, FAILED, DEAD_LETTERED
}
