package com.back.domain.workout.session.controller;

import com.back.domain.workout.session.dto.WorkoutSessionCompleteRequest;
import com.back.domain.workout.session.dto.WorkoutSessionCompleteResponse;
import com.back.domain.workout.session.dto.WorkoutSessionResponse;
import com.back.domain.workout.session.service.WorkoutSessionService;
import com.back.global.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{sessionId}/complete")
    public ResponseEntity<WorkoutSessionCompleteResponse> complete(@AuthenticationPrincipal UserPrincipal principal,
                                                                   @PathVariable Long sessionId,
                                                                   @RequestBody WorkoutSessionCompleteRequest request) {
        return ResponseEntity.ok(workoutSessionService.completeSession(principal.getId(), sessionId, request));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long sessionId) {
        workoutSessionService.cancelSession(principal.getId(), sessionId);
        return ResponseEntity.noContent().build();
    }




}
