package com.back.domain.workout.session.service;

import com.back.domain.routine.routine.dto.RoutineCreateRequest;
import com.back.domain.routine.routine.entity.Routine;
import com.back.domain.routine.routine.repository.RoutineRepository;
import com.back.domain.routine.routine.service.RoutineService;
import com.back.domain.routine.routineItem.dto.RoutineItemCreateRequest;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.repository.UserRepository;
import com.back.domain.workout.session.dto.*;
import com.back.domain.workout.session.entity.WorkoutSession;
import com.back.domain.workout.session.repository.WorkoutSessionRepository;
import com.back.domain.workout.set.entity.WorkoutSet;
import com.back.domain.workout.set.repository.WorkoutSetRepository;
import com.back.domain.workout.set.repository.WorkoutSetRepository.RoutineExerciseSummary;
import com.back.global.exception.type.BadRequestException;
import com.back.global.exception.type.ForbiddenException;
import com.back.global.exception.type.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final RoutineService routineService;
    private final WorkoutSetRepository workoutSetRepository;

    @Transactional
    public WorkoutSessionResponse startRoutineSession(Long userId, Long routineId) {
        if (workoutSessionRepository.existsByUser_IdAndCompletedAtIsNull(userId)) {
            throw new BadRequestException("이미 진행중인 운동이 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 루틴입니다."));

        if (!routine.getUser().getId().equals(userId)) {
            throw new ForbiddenException("루틴 소유자가 아닙니다.");
        }

        WorkoutSession workoutSession = WorkoutSession.start(user, routine);
        workoutSessionRepository.save(workoutSession);

        return WorkoutSessionResponse.from(workoutSession);
    }

    @Transactional
    public WorkoutSessionResponse startFreeSession(Long userId) {
        if (workoutSessionRepository.existsByUser_IdAndCompletedAtIsNull(userId)) {
            throw new BadRequestException("이미 진행중인 운동이 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        WorkoutSession workoutSession = WorkoutSession.start(user, null);
        workoutSessionRepository.save(workoutSession);

        return WorkoutSessionResponse.from(workoutSession);
    }

    @Transactional
    public WorkoutSessionCompleteResponse completeSession(Long userId, Long sessionId, WorkoutSessionCompleteRequest request) {
        WorkoutSession workoutSession = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 세션입니다."));

        if (!workoutSession.getUser().getId().equals(userId)) {
            throw new ForbiddenException("본인의 운동 세션만 완료할 수 있습니다.");
        }

        if (workoutSession.isCompleted()) { // 이미 완료된 세션이면 그대로 리턴
            return WorkoutSessionCompleteResponse.from(workoutSession);
        }

        workoutSession.complete(LocalDateTime.now());   // 완료 처리

        WorkoutCompleteAction action = (request == null || request.action() == null) ?
                WorkoutCompleteAction.RECORD_ONLY : request.action();

        // 기록만 저장이면 이미 완료 처리 되었으니 바로 리턴
        if (action == WorkoutCompleteAction.RECORD_ONLY) {
            return WorkoutSessionCompleteResponse.from(workoutSession);
        }

        // 루틴 기반 세션이면 새 루틴 생성은 허용하지 않음
        if (workoutSession.getRoutine() != null) {
            if (action == WorkoutCompleteAction.CREATE_ROUTINE_AND_RECORD) {
                throw new BadRequestException("루틴 기반 세션에서는 새로운 루틴을 생성할 수 없습니다.");
            }
            return WorkoutSessionCompleteResponse.from(workoutSession);
        }

        // 여기까지 왔으면 루틴까지 생성하는 요청임
        String routineTitle = request.routineTitle();
        if (routineTitle == null || routineTitle.isBlank()) {
            throw new BadRequestException("루틴 이름을 입력 해주세요.");
        }

        Routine newRoutine = createRoutineFromSession(userId, workoutSession, routineTitle);    // 세션을 기반으로 루틴 생성
        workoutSession.setRoutine(newRoutine);      // 지금 운동 세션도 루틴에 넣기

        return WorkoutSessionCompleteResponse.from(workoutSession);
    }

    @Transactional
    public void cancelSession(Long userId, Long sessionId) {
        WorkoutSession workoutSession = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 세션입니다."));

        if (!workoutSession.getUser().getId().equals(userId)) {
            throw new ForbiddenException("본인의 운동 세션만 취소할 수 있습니다.");
        }

        if (workoutSession.isCompleted()) {
            throw new BadRequestException("이미 완료된 운동은 취소할 수 없습니다.");
        }

        workoutSessionRepository.delete(workoutSession);
    }

    @Transactional(readOnly = true)
    public Page<WorkoutSessionListItemResponse> getWorkoutSessions(Long userId, Pageable pageable) {

        Page<WorkoutSession> sessions = workoutSessionRepository.findByUser_IdAndCompletedAtIsNotNullOrderByCompletedAtDesc(userId, pageable);

        return sessions.map(WorkoutSessionListItemResponse::from);
    }

    @Transactional(readOnly = true)
    public WorkoutSessionDetailResponse getWorkoutSessionDetail(Long userId, Long sessionId) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 세션입니다."));

        if (!session.getUser().getId().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        List<WorkoutSet> sets = workoutSetRepository.findByWorkoutSession_IdOrderByCreatedAtAsc(sessionId);

        var grouped = sets.stream().collect(java.util.stream.Collectors.groupingBy(
                set -> set.getExercise().getId(),
                java.util.LinkedHashMap::new,
                java.util.stream.Collectors.toList()
        ));

        List<WorkoutExerciseDetailResponse> exercises = grouped.values().stream()
                .map(exerciseSets -> {
                    WorkoutSet first = exerciseSets.get(0);
                    List<WorkoutSetDetailResponse> setResponses = exerciseSets.stream()
                            .map(WorkoutSetDetailResponse::from)
                            .toList();

                    return new WorkoutExerciseDetailResponse(
                            first.getExercise().getId(),
                            first.getExerciseName(),
                            setResponses
                    );
                })
                .toList();

        return WorkoutSessionDetailResponse.of(session, exercises);
    }



    private Routine createRoutineFromSession(Long userId, WorkoutSession workoutSession, String routineTitle) {

        // 1. 세션 내 운동별 요약 집계
        List<RoutineExerciseSummary> summaries = workoutSetRepository.summarizeBySession(workoutSession.getId());
        if (summaries.isEmpty()) {
            throw new BadRequestException("세트 기록이 없는 세션에는 루틴을 생성할 수 없습니다.");
        }

        // 2. RoutineItemCreateRequest 로 변환
        List<RoutineItemCreateRequest> routineItems = summaries.stream()
                .map(s -> new RoutineItemCreateRequest(
                        s.getExerciseId(),
                        s.getMaxSetNumber(),
                        0       // restSeconds 는 우선 0으로
                ))
                .toList();

        // 3. RoutineCreateRequest 구성
        RoutineCreateRequest req = new RoutineCreateRequest(
                routineTitle,
                null,   // description 은 null
                routineItems
        );

        return routineService.createRoutine(userId, req);
    }
}
