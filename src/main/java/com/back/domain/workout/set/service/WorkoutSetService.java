package com.back.domain.workout.set.service;

import com.back.domain.exercise.entity.Exercise;
import com.back.domain.exercise.repository.ExerciseRepository;
import com.back.domain.workout.session.entity.WorkoutSession;
import com.back.domain.workout.session.repository.WorkoutSessionRepository;
import com.back.domain.workout.set.dto.WorkoutSetCompleteRequest;
import com.back.domain.workout.set.dto.WorkoutSetResponse;
import com.back.domain.workout.set.entity.WorkoutSet;
import com.back.domain.workout.set.repository.WorkoutSetRepository;
import com.back.global.exception.type.ForbiddenException;
import com.back.global.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutSetService {
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public WorkoutSetResponse createSet(Long userId, Long sessionId, WorkoutSetCompleteRequest request) {
        WorkoutSession workoutSession = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 세션입니다."));

        if (!workoutSession.getUser().getId().equals(userId)) {
            throw new ForbiddenException("본인의 운동 세션에만 세트를 추가할 수 있습니다.");
        }

        Exercise exercise = exerciseRepository.findById(request.exerciseId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 종목입니다."));

        int nextSetNumber = workoutSetRepository.findMaxSetNumber(sessionId, request.exerciseId())
                .orElse(0) + 1;

        WorkoutSet workoutSet = WorkoutSet.create(
                workoutSession,
                exercise,
                nextSetNumber,
                request.weight(),
                request.reps());

        workoutSetRepository.save(workoutSet);

        return WorkoutSetResponse.from(workoutSet);
    }
}
