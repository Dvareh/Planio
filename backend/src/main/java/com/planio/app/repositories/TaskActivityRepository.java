package com.planio.app.repositories;

import com.planio.app.entity.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
    List<TaskActivity> findByTask_IdOrderByCreatedAtDesc(Long taskId);
}
