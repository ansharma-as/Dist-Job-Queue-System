package com.jobqueue.api.repository;

import com.jobqueue.api.entity.Job;
import com.jobqueue.api.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    long countByStatusIn(List<JobStatus> statuses);
    long countByStatus(JobStatus status);
    List<Job> findTop10ByOrderByCreatedAtDesc();
}
