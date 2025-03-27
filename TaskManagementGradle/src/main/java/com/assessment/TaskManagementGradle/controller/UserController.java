package com.assessment.TaskManagementGradle.controller;

import com.assessment.TaskManagementGradle.dto.ApiResponse;
import com.assessment.TaskManagementGradle.dto.UserResponseDTO;
import com.assessment.TaskManagementGradle.entity.User;
import com.assessment.TaskManagementGradle.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users with 200 OK response
    @GetMapping
    // Only Admin view all user
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        // Convert User to UserResponseDto
        List<UserResponseDTO> userDtos = users.stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(userDtos, 200, "Users retrieved successfully"));
    }

    // Get user by ID with 404 handling
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            UserResponseDTO userDto = new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
            return ResponseEntity.ok(new ApiResponse<>(userDto, 200, "User found"));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse<>(null, 404, "User not found"));
        }
    }

    // Delete user with appropriate response
    @DeleteMapping("/{id}")
    // Only Admin can delete user
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse<>(null, 200, "User deleted successfully"));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse<>(null, 404, "User not found"));
        }
    }

}
