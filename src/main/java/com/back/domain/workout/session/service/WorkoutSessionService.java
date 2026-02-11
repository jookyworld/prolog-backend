package com.back.domain.workout.session.service;

import com.back.domain.routine.routine.entity.Routine;
import com.back.domain.routine.routine.repository.RoutineRepository;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
import com.back.domain.workout.session.dto.WorkoutSessionResponse;
import com.back.domain.workout.session.entity.WorkoutSession;
import com.back.domain.workout.session.repository.WorkoutSessionRepository;
import com.back.global.exception.type.BadRequestException;
import com.back.global.exception.type.ForbiddenException;
import com.back.global.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;

    @Transactional
    public WorkoutSessionResponse startRoutineSession(Long userId, Long routineId) {
        if (workoutSessionRepository.existsByUser_IdAndCompletedAtIsNull(userId)) {
            throw new BadRequestException("이미 진행중인 운동이 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 루틴입니다."));

        if (!routine.getUser().getId().equals(userId)) {
            throw new ForbiddenException("루틴 소유자가 아닙니다.");
        }

        WorkoutSession workoutSession = WorkoutSession.start(user, routine);
        workoutSessionRepository.save(workoutSession);

        return WorkoutSessionResponse.from(workoutSession);
    }

    @Transactional
    public WorkoutSessionResponse startFreeSession(Long userId) {
        if (workoutSessionRepository.existsByUser_IdAndCompletedAtIsNull(userId)) {
            throw new BadRequestException("이미 진행중인 운동이 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        WorkoutSession workoutSession = WorkoutSession.start(user, null);
        workoutSessionRepository.save(workoutSession);

        return WorkoutSessionResponse.from(workoutSession);
    }
}
