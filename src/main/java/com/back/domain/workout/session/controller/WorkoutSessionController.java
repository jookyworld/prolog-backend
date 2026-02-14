package com.back.domain.workout.session.controller;

import com.back.domain.workout.session.dto.*;
import com.back.domain.workout.session.service.WorkoutSessionService;
import com.back.global.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workouts/sessions")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> startSession(@AuthenticationPrincipal UserPrincipal principal,
                                                                @RequestBody WorkoutSessionStartRequest request) {
        WorkoutSessionResponse response = workoutSessionService.startSession(principal.getId(), request.routineId());
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

    @GetMapping
    public ResponseEntity<Page<WorkoutSessionListItemResponse>> getMySessions(@AuthenticationPrincipal UserPrincipal principal,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(workoutSessionService.getWorkoutSessions(principal.getId(), pageable));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<WorkoutSessionDetailResponse> getDetail(@AuthenticationPrincipal UserPrincipal principal,
                                                                  @PathVariable Long sessionId) {
        return ResponseEntity.ok(workoutSessionService.getWorkoutSessionDetail(principal.getId(), sessionId));
    }

}
