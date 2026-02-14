package com.back.domain.workout.set.controller;

import com.back.domain.workout.set.dto.WorkoutSetCompleteRequest;
import com.back.domain.workout.set.dto.WorkoutSetResponse;
import com.back.domain.workout.set.service.WorkoutSetService;
import com.back.global.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workouts/sessions/{sessionId}/sets")
public class WorkoutSetController {
    private final WorkoutSetService workoutSetService;

    @PostMapping
    public ResponseEntity<WorkoutSetResponse> createSet(@AuthenticationPrincipal UserPrincipal principal,
                                                        @PathVariable Long sessionId,
                                                        @RequestBody WorkoutSetCompleteRequest request) {
        WorkoutSetResponse response = workoutSetService.createSet(principal.getId(), sessionId, request);
        return ResponseEntity.ok(response);
    }
}
