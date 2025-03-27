package com.assessment.TaskManagementGradle.repository;

import com.assessment.TaskManagementGradle.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
}
