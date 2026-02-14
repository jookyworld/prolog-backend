package com.back.domain.workout.session.repository;

import com.back.domain.workout.session.entity.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    boolean existsByUser_IdAndCompletedAtIsNull(Long userId);

    java.util.Optional<WorkoutSession> findByUser_IdAndCompletedAtIsNull(Long userId);

    Page<WorkoutSession> findByUser_IdAndCompletedAtIsNotNullOrderByCompletedAtDesc(Long userId, Pageable pageable);

    java.util.Optional<WorkoutSession> findTopByUser_IdAndRoutine_IdAndCompletedAtIsNotNullOrderByCompletedAtDesc(Long userId, Long routineId);
}
