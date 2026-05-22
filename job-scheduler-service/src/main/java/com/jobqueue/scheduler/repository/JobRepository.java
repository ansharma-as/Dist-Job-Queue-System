package com.jobqueue.scheduler.repository;

import com.jobqueue.scheduler.entity.Job;
import com.jobqueue.scheduler.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findTop100ByStatusOrderByCreatedAtAsc(JobStatus status);
}
