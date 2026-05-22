package com.jobqueue.api.dto;

public class JobStats {
    public long totalProcessed;
    public long activeWorkers;
    public long queueDepth;
    public double errorRate;
}
