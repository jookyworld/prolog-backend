package com.back.domain.workout.session.dto;

public record WorkoutSessionCompleteRequest(
        WorkoutCompleteAction action,
        String routineTitle
) {
}
