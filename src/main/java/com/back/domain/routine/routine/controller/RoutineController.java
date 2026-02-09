package com.back.domain.routine.routine.controller;

import com.back.domain.routine.routine.dto.RoutineCreateRequest;
import com.back.domain.routine.routine.dto.RoutineDetailResponse;
import com.back.domain.routine.routine.dto.RoutineResponse;
import com.back.domain.routine.routine.service.RoutineService;
import com.back.global.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routines")
public class RoutineController {
    private final RoutineService routineService;

    @PostMapping
    public RoutineResponse createRoutine(@AuthenticationPrincipal UserPrincipal principal,
                                                         @Valid @RequestBody RoutineCreateRequest request) {
        return routineService.createRoutine(principal.getId(), request);
    }

    @GetMapping("/{routineId}")
    public RoutineDetailResponse getRoutineDetail(@AuthenticationPrincipal UserPrincipal principal,
                                                  @PathVariable Long routineId) {
        return routineService.getRoutineDetail(principal.getId(), routineId);
    }

}
