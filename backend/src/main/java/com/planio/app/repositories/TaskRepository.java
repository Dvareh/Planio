package com.planio.app.repositories;

import com.planio.app.entity.Task;
import com.planio.app.entity.TaskPriority;
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
            "assignedUser",
    })
    @Query(
            value = """
            SELECT DISTINCT t FROM Task t LEFT JOIN t.labels l
            WHERE
            (
                t.board.owner = :user
                OR :user MEMBER OF t.board.participants
            )
            AND (:boardId IS NULL OR t.board.id = :boardId)
            AND (
                :search IS NULL OR :search = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (
                    :status IS NULL OR t.status = :status
            )
            AND (
                    :priority IS NULL OR t.priority = :priority
            )
            AND (
                :assignedUserId IS NULL OR t.assignedUser.id = :assignedUserId
            )
            AND (
                :labelId IS NULL OR l.id = :labelId
            )
            AND (
                CAST(:dueDateFrom AS date) IS NULL OR t.dueDate >= :dueDateFrom
            )
            AND (
                CAST(:dueDateTo AS date) IS NULL OR t.dueDate <= :dueDateTo
            )
        """,
            countQuery = """
            SELECT COUNT(DISTINCT t.id) FROM Task t LEFT JOIN t.labels l
            WHERE
            (
                t.board.owner = :user OR :user MEMBER OF t.board.participants
            )
            AND (
                    :boardId IS NULL OR t.board.id = :boardId
            )
            AND (
                :search IS NULL OR :search = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (
                    :status IS NULL OR t.status = :status
            )
            AND (
                    :priority IS NULL OR t.priority = :priority
            )
            AND (
                :assignedUserId IS NULL OR t.assignedUser.id = :assignedUserId
            )
            AND (
                :labelId IS NULL OR l.id = :labelId
            )
            AND (
                CAST(:dueDateFrom AS date) IS NULL OR t.dueDate >= :dueDateFrom
            )
            AND (
                CAST(:dueDateTo AS date) IS NULL OR t.dueDate <= :dueDateTo
            )
        """
    )
    Page<Task> searchTasks(
            @Param("user") User user,
            @Param("boardId") Long boardId,
            @Param("search") String search,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("assignedUserId") Long assignedUserId,
            @Param("labelId") Long labelId,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            Pageable pageable
    );

    List<Task> findByDueDateBetween(LocalDate start, LocalDate end);

}
