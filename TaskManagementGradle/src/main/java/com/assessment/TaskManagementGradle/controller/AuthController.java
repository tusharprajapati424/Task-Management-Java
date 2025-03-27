package com.assessment.TaskManagementGradle.controller;
import com.assessment.TaskManagementGradle.config.JwtUtil;
import com.assessment.TaskManagementGradle.dto.ApiResponse;
import com.assessment.TaskManagementGradle.dto.LoginRequestDTO;
import com.assessment.TaskManagementGradle.dto.RegisterRequestDTO;
import com.assessment.TaskManagementGradle.dto.UserResponseDTO;
import com.assessment.TaskManagementGradle.entity.User;
import com.assessment.TaskManagementGradle.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PreAuthorize("hasRole('ADMIN')") // Only Admin can access this API
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestHeader("Authorization") String token,
                                                                 @Valid @RequestBody RegisterRequestDTO registerRequest) {

        System.out.println("register called :: "+registerRequest.toString());

        // Validate token (throws exception if invalid)
        jwtUtil.validateToken(token);

        // Extract email from token
        String email = jwtUtil.extractEmail(token);

        // Fetch user from database based on email
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, HttpStatus.UNAUTHORIZED.value(), "User not found in the database"));
        }

        String role = userOptional.get().getRole().toString();  // Convert Enum to String

        // Only allow Admins to register new users
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, HttpStatus.FORBIDDEN.value(), "Only Admins can register users"));
        }

        // Check if email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, HttpStatus.BAD_REQUEST.value(), "Email already exists"));
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Encode password
        user.setRole(registerRequest.getRole()); // Set Role

        // Save user to DB
        User savedUser = userRepository.save(user);

        // Prepare response
        UserResponseDTO userResponseDTO = new UserResponseDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
        ApiResponse<UserResponseDTO> response = new ApiResponse<>(userResponseDTO, HttpStatus.CREATED.value(), "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,String>>>  login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            System.out.println("optional user :: "+user.get().toString());
            String token = jwtUtil.generateToken(user.get().getId(), user.get().getEmail(), user.get().getUsername(), user.get().getRole());

            // Wrapping token inside a Map
            Map<String, String> data = new HashMap<>();
            data.put("token", token);

            ApiResponse<Map<String, String>> response = new ApiResponse<>(data, HttpStatus.OK.value(), "User login successfully");
            return ResponseEntity.ok(response);
        }

        ApiResponse<Map<String, String>> errorResponse = new ApiResponse<>(null, HttpStatus.UNAUTHORIZED.value(), "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
