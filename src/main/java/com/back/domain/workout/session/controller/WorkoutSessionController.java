package com.back.domain.workout.session.controller;

import com.back.domain.workout.session.dto.WorkoutSessionResponse;
import com.back.domain.workout.session.service.WorkoutSessionService;
import com.back.global.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workouts/sessions")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    @PostMapping("/routine/{routineId}")
    public ResponseEntity<WorkoutSessionResponse> startRoutineSession(@AuthenticationPrincipal UserPrincipal principal,
                                                                      @PathVariable Long routineId) {
        WorkoutSessionResponse response = workoutSessionService.startRoutineSession(principal.getId(), routineId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/free")
    public ResponseEntity<WorkoutSessionResponse> startFreeSession(@AuthenticationPrincipal UserPrincipal principal) {
        WorkoutSessionResponse response = workoutSessionService.startFreeSession(principal.getId());
        return ResponseEntity.ok(response);
    }


}
