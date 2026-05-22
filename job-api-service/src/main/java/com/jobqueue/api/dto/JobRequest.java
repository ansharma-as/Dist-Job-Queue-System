package com.jobqueue.api.dto;

public class JobRequest {
    private String type;
    private String payload;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
