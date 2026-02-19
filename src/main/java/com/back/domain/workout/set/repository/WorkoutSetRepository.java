package com.back.domain.workout.set.repository;

import com.back.domain.workout.set.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    @Query("""
        select
            ws.exercise.id as exerciseId,
            max(ws.setNumber) as maxSetNumber,
            min(ws.createdAt) as firstCreatedAt
        from WorkoutSet ws
        where ws.workoutSession.id = :sessionId
        group by ws.exercise.id
        order by firstCreatedAt asc
    """)
    List<RoutineExerciseSummary> summarizeBySession(Long sessionId);

    interface RoutineExerciseSummary {
        Long getExerciseId();
        Integer getMaxSetNumber();
        LocalDateTime getFirstCreatedAt();
    }

    List<WorkoutSet> findByWorkoutSession_IdOrderByCreatedAtAsc(Long sessionId);

    void deleteAllByWorkoutSession_User_Id(Long userId);
}
