package com.back.domain.exercise.repository;

import com.back.domain.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    boolean existsByName(String name);
}
