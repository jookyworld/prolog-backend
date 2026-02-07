package com.back.domain.exercise.service;

import com.back.domain.exercise.dto.ExerciseCreateRequest;
import com.back.domain.exercise.dto.ExerciseResponse;
import com.back.domain.exercise.entity.Exercise;
import com.back.domain.exercise.repository.ExerciseRepository;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ExerciseResponse> getAllExercises() {
        return exerciseRepository.findAll()
                .stream()
                .map(ExerciseResponse::from)
                .toList();
    }

    @Transactional
    public ExerciseResponse createCustomExercise(Long userId, ExerciseCreateRequest request) {
        if (exerciseRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("이미 존재하는 종목입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + userId));

        Exercise exercise = Exercise.builder()
                .name(request.name())
                .bodyPart(request.bodyPart())
                .partDetail(request.partDetail())
                .custom(true)
                .createdBy(user)
                .build();

        Exercise saved = exerciseRepository.save(exercise);
        return ExerciseResponse.from(saved);
    }

    @Transactional
    public ExerciseResponse createAdminExercise(ExerciseCreateRequest request) {
        if (exerciseRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("이미 존재하는 종목입니다.");
        }

        Exercise exercise = Exercise.builder()
                .name(request.name())
                .bodyPart(request.bodyPart())
                .partDetail(request.partDetail())
                .custom(false)
                .createdBy(null)
                .build();

        Exercise saved = exerciseRepository.save(exercise);
        return ExerciseResponse.from(saved);
    }
}
