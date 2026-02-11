package com.back.domain.workout.set.dto;

public record WorkoutSetCreateRequest(
        Long exerciseId,
        int weight,
        int reps
) {
}
