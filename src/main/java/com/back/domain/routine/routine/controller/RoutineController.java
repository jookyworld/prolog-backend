package com.back.domain.routine.routine.controller;

import com.back.domain.routine.routine.dto.RoutineCreateRequest;
import com.back.domain.routine.routine.dto.RoutineDetailResponse;
import com.back.domain.routine.routine.dto.RoutineResponse;
import com.back.domain.routine.routine.dto.RoutineStatusFilter;
import com.back.domain.routine.routine.service.RoutineService;
import com.back.global.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<RoutineResponse> getMyRoutines(@AuthenticationPrincipal UserPrincipal principal,
                                               @RequestParam(name = "status", defaultValue = "ACTIVE")RoutineStatusFilter status) {
        return routineService.getMyRoutines(principal.getId(), status);
    }

    @PatchMapping("/{routineId}/activate")
    public RoutineResponse activateRoutine(@AuthenticationPrincipal UserPrincipal principal,
                                           @PathVariable Long routineId) {
        return routineService.activateRoutine(principal.getId(), routineId);
    }

    @PatchMapping("/{routineId}/archive")
    public RoutineResponse archiveRoutine(@AuthenticationPrincipal UserPrincipal principal,
                                          @PathVariable Long routineId) {
        return routineService.archiveRoutine(principal.getId(), routineId);
    }

}
