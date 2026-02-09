package com.back.domain.routine.routine.service;

import com.back.domain.exercise.entity.Exercise;
import com.back.domain.exercise.repository.ExerciseRepository;
import com.back.domain.routine.routine.dto.RoutineCreateRequest;
import com.back.domain.routine.routine.dto.RoutineResponse;
import com.back.domain.routine.routine.entity.Routine;
import com.back.domain.routine.routine.repository.RoutineRepository;
import com.back.domain.routine.routineItem.dto.RoutineItemCreateRequest;
import com.back.domain.routine.routineItem.entity.RoutineItem;
import com.back.domain.routine.routineItem.repository.RoutineItemRepository;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final RoutineItemRepository routineItemRepository;

    @Transactional
    public RoutineResponse createRoutine(Long userId, RoutineCreateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Routine routine = Routine.builder()
                .user(user)
                .title(request.title())
                .description(request.description())
                .active(true)
                .build();

        routineRepository.save(routine);

        int order = 1;
        for (RoutineItemCreateRequest i : request.routineItems()) {
            Exercise exercise = exerciseRepository.findById(i.exerciseId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 운동 종목입니다."));

            RoutineItem routineItem = RoutineItem.builder()
                    .routine(routine)
                    .exercise(exercise)
                    .orderInRoutine(order++)
                    .sets(i.sets())
                    .restSeconds(i.restSeconds())
                    .build();

            routineItemRepository.save(routineItem);
        }

        return RoutineResponse.from(routine);
    }
}
