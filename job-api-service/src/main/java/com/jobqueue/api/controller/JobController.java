package com.jobqueue.api.controller;

import com.jobqueue.api.dto.JobRequest;
import com.jobqueue.api.dto.JobStats;
import com.jobqueue.api.entity.Job;
import com.jobqueue.api.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin("*")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<Job> submitJob(@RequestBody JobRequest request) {
        Job job = jobService.submitJob(request);
        return ResponseEntity.ok(job);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Job>> submitJobs(@RequestBody List<JobRequest> requests) {
        List<Job> jobs = jobService.submitJobs(requests);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJob(id));
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Job>> getRecentJobs() {
        return ResponseEntity.ok(jobService.getRecentJobs());
    }
    
    @GetMapping("/stats")
    public ResponseEntity<JobStats> getStats() {
        return ResponseEntity.ok(jobService.getStats());
    }
}
