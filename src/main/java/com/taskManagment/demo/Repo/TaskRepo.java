package com.taskManagment.demo.Repo;

import com.taskManagment.demo.Entity.Task;
import com.taskManagment.demo.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepo extends JpaRepository<Task,Long> {
    Page<Task> findByUser(User user, Pageable pageable);
    List<Task> findByUser(User user);

    // For search functionality with paging
    Page<Task> findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        User user, String title, String description, Pageable pageable);

    // For notification service - find tasks with deadlines in a specific time window
    @Query("SELECT t FROM Task t WHERE t.deadline BETWEEN :start AND :end")
    List<Task> findTasksWithDeadlineBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // For cleanup - find tasks that are past their deadline
    @Query("SELECT t FROM Task t WHERE t.deadline < :now")
    List<Task> findTasksWithDeadlineBefore(@Param("now") LocalDateTime now);

    // For filtering with paging - complex query with optional parameters
    @Query("SELECT t FROM Task t WHERE t.user = :user " +
           "AND (:start IS NULL OR t.deadline >= :start) " +
           "AND (:end IS NULL OR t.deadline <= :end) " +
           "AND (:status IS NULL OR :status = '' OR LOWER(t.status) = LOWER(:status))")
    Page<Task> findTasksWithFilters(@Param("user") User user,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("status") String status,
                                   Pageable pageable);
}

