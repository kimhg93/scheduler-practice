package com.practice.scheduler.repository;

import com.practice.scheduler.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchedulerRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByJobId(String jobId);

}
