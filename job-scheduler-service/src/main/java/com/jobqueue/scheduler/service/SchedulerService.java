package com.jobqueue.scheduler.service;

import com.jobqueue.scheduler.entity.Job;
import com.jobqueue.scheduler.entity.JobStatus;
import com.jobqueue.scheduler.repository.JobRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchedulerService {

    private final JobRepository jobRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SchedulerService(JobRepository jobRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.jobRepository = jobRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void pollAndPublishPendingJobs() {
        List<Job> pendingJobs = jobRepository.findTop100ByStatusOrderByCreatedAtAsc(JobStatus.PENDING);
        
        for (Job job : pendingJobs) {
            job.setStatus(JobStatus.QUEUED);
            jobRepository.save(job);
            
            // Publish to Kafka
            kafkaTemplate.send("job-pending", job.getType(), job.getId().toString());
        }
        
        if (!pendingJobs.isEmpty()) {
            System.out.println("Published " + pendingJobs.size() + " jobs to Kafka.");
        }
    }
}
