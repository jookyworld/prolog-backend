package com.back.domain.workout.session.repository;

import com.back.domain.workout.session.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    boolean existsByUser_IdAndCompletedAtIsNull(Long userId);
}
