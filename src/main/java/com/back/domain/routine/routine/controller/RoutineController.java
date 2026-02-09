package com.back.domain.routine.routine.controller;

import com.back.domain.routine.routine.dto.RoutineCreateRequest;
import com.back.domain.routine.routine.dto.RoutineResponse;
import com.back.domain.routine.routine.service.RoutineService;
import com.back.global.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
