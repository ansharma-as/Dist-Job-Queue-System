package com.jobqueue.worker.service;

import com.jobqueue.worker.entity.Job;
import com.jobqueue.worker.entity.JobStatus;
import com.jobqueue.worker.repository.JobRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkerService {

    private final JobRepository jobRepository;

    public WorkerService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @KafkaListener(topics = "job-pending", groupId = "worker-group", concurrency = "10")
    public void processJob(ConsumerRecord<String, String> record) {
        UUID jobId = UUID.fromString(record.value());
        
        System.out.println("Worker starting job: " + jobId);
        
        try {
            markJobAsProcessing(jobId);
            
            // Simulating heavy work (e.g., video processing)
            // Thread.sleep(3000 + (long)(Math.random() * 2000));
            
            // 10% chance of random failure
            // if (Math.random() < 0.1) {
            //     throw new RuntimeException("Random simulated failure");
            // }
            
            markJobAsCompleted(jobId);
            System.out.println("Worker completed job: " + jobId);
        } catch (Exception e) {
            System.err.println("Job failed: " + e.getMessage());
            markJobAsFailed(jobId, e.getMessage());
        }
    }

    @Transactional
    public void markJobAsProcessing(UUID jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.PROCESSING);
        jobRepository.save(job);
    }

    @Transactional
    public void markJobAsCompleted(UUID jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.COMPLETED);
        jobRepository.save(job);
    }
    
    @Transactional
    public void markJobAsFailed(UUID jobId, String error) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setErrorMessage(error);
        job.setAttempts(job.getAttempts() + 1);
        if (job.getAttempts() >= job.getMaxAttempts()) {
            job.setStatus(JobStatus.DEAD_LETTERED);
        } else {
            job.setStatus(JobStatus.FAILED);
        }
        jobRepository.save(job);
    }
}
