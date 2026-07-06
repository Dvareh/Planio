package com.planio.app.repositories;

import com.planio.app.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    public List<Task> findTasksByBoard_Owner_Id(Long ownerId);
    public List<Task> findTasksByBoard_Participants_Id(Long userId);
}
