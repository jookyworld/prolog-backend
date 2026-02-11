package com.back.domain.workout.set.repository;

import com.back.domain.workout.set.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    @Query("""
            select max(ws.setNumber)
            from WorkoutSet ws
            where ws.workoutSession.id = :sessionId
            and ws.exercise.id = :exerciseId
            """)
    Optional<Integer> findMaxSetNumber(Long sessionId, Long exerciseId);
}
