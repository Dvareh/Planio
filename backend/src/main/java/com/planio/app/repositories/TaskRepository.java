package com.planio.app.repositories;

import com.planio.app.entity.Task;
import com.planio.app.entity.TaskStatus;
import com.planio.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {
            "board",
            "assignedUser"
    })
    List<Task> findByAssignedUser(User assignedUser);

    @EntityGraph(attributePaths = {
            "board",
            "assignedUser"
    })
    @Query("""
        SELECT t FROM Task t
        WHERE
        (
            t.board.owner = :user
            OR :user MEMBER OF t.board.participants
        )
        AND
        (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))
        AND
        (:status IS NULL OR t.status = :status)
    """)
    Page<Task> searchTasks(
            @Param("user") User user,
            @Param("search") String search,
            @Param("status") TaskStatus status,
            Pageable pageable
    );

    List<Task> findByDueDateBetween(LocalDate start, LocalDate end);

}
