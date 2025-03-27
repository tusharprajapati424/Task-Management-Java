package com.assessment.TaskManagementGradle.controller;

import com.assessment.TaskManagementGradle.dto.ApiResponse;
import com.assessment.TaskManagementGradle.dto.CreateTaskRequestDTO;
import com.assessment.TaskManagementGradle.dto.RegisterRequestDTO;
import com.assessment.TaskManagementGradle.dto.UpdateTaskRequestDTO;
import com.assessment.TaskManagementGradle.entity.Task;
import com.assessment.TaskManagementGradle.entity.User;
import com.assessment.TaskManagementGradle.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import com.assessment.TaskManagementGradle.config.JwtUtil;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;
    private final JwtUtil jwtUtil;

    public TaskController(TaskService taskService,JwtUtil jwtUtil) {
        this.taskService = taskService;
        this.jwtUtil = jwtUtil;
    }

    // Get all tasks
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')") // Only ADMIN and EMPLOYEE can access
    @GetMapping
    public ResponseEntity<ApiResponse<List<Task>>> getAllTasks(@RequestHeader("Authorization") String token){
        List<Task> tasks = taskService.getAllTasks();

        if (tasks.isEmpty()) {
            ApiResponse<List<Task>> response = new ApiResponse<>(Collections.emptyList(), HttpStatus.OK.value(), "No tasks found");
            return ResponseEntity.ok(response);
        }

        ApiResponse<List<Task>> response = new ApiResponse<>(tasks, HttpStatus.OK.value(), "Tasks retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // Create a new task - Requires userId in the path
    // Only Admin can create a task
    @PreAuthorize("hasRole('ADMIN')") // Only Admin can access this API
    @PostMapping
    public ResponseEntity<ApiResponse<Task>> createTask(@RequestHeader("Authorization") String token,
                                           @Valid @RequestBody CreateTaskRequestDTO createTaskRequest) {

        System.out.println("createTask called :: "+ createTaskRequest.toString());

        // Convert DTO to Entity & Assign User
        Task task = new Task();
        task.setTitle(createTaskRequest.getTitle());
        task.setDescription(createTaskRequest.getDescription());
        task.setStatus(createTaskRequest.getStatus());

        try {
            // Call Service Layer to Handle Task Creation
            Task createdTask = taskService.createTask(task, createTaskRequest.getAssignedUserId());

            ApiResponse<Task> response = new ApiResponse<>(createdTask, HttpStatus.CREATED.value(), "Task created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            ApiResponse<Task> errorResponse = new ApiResponse<>(null, HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Get task by ID with 404 handling
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')") // Only ADMIN and EMPLOYEE can access
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>>  getTaskById(@RequestHeader("Authorization") String token,
                                                          @PathVariable Long id){
        Task task = taskService.getTaskById(id);

        if (task == null) {
            ApiResponse<Task> errorResponse = new ApiResponse<>(null, HttpStatus.NOT_FOUND.value(), "Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        ApiResponse<Task> response = new ApiResponse<>(task, HttpStatus.OK.value(), "Task retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('EMPLOYEE')") // Only EMPLOYEES can update their assigned tasks
    // Update task by ID
    // Only Assigned Employee can update their own task
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> updateTaskById(@PathVariable Long id,
                                                @RequestHeader("Authorization") String token,
                                                @Valid @RequestBody UpdateTaskRequestDTO updateTaskRequest){

        // Extract user ID from JWT token
        Long loggedInUserId = jwtUtil.extractUserId(token);

        // Fetch the task from DB
        Task existingTask = taskService.getTaskById(id);

        if (existingTask == null) {
            ApiResponse<Task> errorResponse = new ApiResponse<>(null, HttpStatus.NOT_FOUND.value(), "Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Check if the logged-in user is the assigned employee
        if (!existingTask.getAssignedUser().getId().equals(loggedInUserId)) {
            ApiResponse<Task> errorResponse = new ApiResponse<>(null, HttpStatus.FORBIDDEN.value(), "You can only update your assigned tasks");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        // Update task fields from DTO
        existingTask.setTitle(updateTaskRequest.getTitle());
        existingTask.setDescription(updateTaskRequest.getDescription());
        existingTask.setStatus(updateTaskRequest.getStatus());

        // Save updated task
        Task updatedTask = taskService.updateTask(id, existingTask);

        ApiResponse<Task> response = new ApiResponse<>(updatedTask, HttpStatus.OK.value(), "Task updated successfully");
        return ResponseEntity.ok(response);

    }

    // Only Admin can delete any task
    @PreAuthorize("hasRole('ADMIN')")
    // Delete task by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@RequestHeader("Authorization") String token,
                                                            @PathVariable Long id) {

        boolean isDeleted = taskService.deleteById(id);

        if (isDeleted) {
            ApiResponse<Void> response = new ApiResponse<>(null, HttpStatus.OK.value(), "Task deleted successfully");
            return ResponseEntity.ok(response);
        }

        ApiResponse<Void> errorResponse = new ApiResponse<>(null, HttpStatus.NOT_FOUND.value(), "Task not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
