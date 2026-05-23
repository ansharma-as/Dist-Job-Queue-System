package com.jobqueue.api.service;

import com.jobqueue.api.dto.JobRequest;
import com.jobqueue.api.dto.JobStats;
import com.jobqueue.api.entity.Job;
import com.jobqueue.api.entity.JobStatus;
import com.jobqueue.api.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Job submitJob(JobRequest request) {
        Job job = new Job();
        job.setType(request.getType());
        job.setPayload(request.getPayload() != null ? request.getPayload() : "{}");
        job.setStatus(JobStatus.PENDING);
        return jobRepository.save(job);
    }

    @Transactional
    public List<Job> submitJobs(List<JobRequest> requests) {
        List<Job> jobs = requests.stream().map(request -> {
            Job job = new Job();
            job.setType(request.getType());
            job.setPayload(request.getPayload() != null ? request.getPayload() : "{}");
            job.setStatus(JobStatus.PENDING);
            return job;
        }).toList();
        return jobRepository.saveAll(jobs);
    }

    @Transactional(readOnly = true)
    public Job getJob(UUID id) {
        return jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }
    
    @Transactional(readOnly = true)
    public List<Job> getRecentJobs() {
        return jobRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    @Transactional(readOnly = true)
    public JobStats getStats() {
        JobStats stats = new JobStats();
        long completed = jobRepository.countByStatus(JobStatus.COMPLETED);
        long failed = jobRepository.countByStatus(JobStatus.FAILED) + jobRepository.countByStatus(JobStatus.DEAD_LETTERED);
        
        stats.totalProcessed = completed + failed;
        stats.queueDepth = jobRepository.countByStatusIn(List.of(JobStatus.PENDING, JobStatus.QUEUED));
        stats.activeWorkers = jobRepository.countByStatus(JobStatus.PROCESSING);
        
        stats.errorRate = stats.totalProcessed > 0 ? (double) failed / stats.totalProcessed * 100.0 : 0.0;
        return stats;
    }
}
