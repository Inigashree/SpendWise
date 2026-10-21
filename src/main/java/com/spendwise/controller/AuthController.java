
package com.spendwise.controller;

import com.spendwise.dto.AuthResponse;
import com.spendwise.dto.LoginRequest;
import com.spendwise.dto.RegisterRequest;
import com.spendwise.entity.User;
import com.spendwise.repository.UserRepository;
import com.spendwise.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message",
                            "An account with this email already exists"));
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(
                passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getEmail());

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null ||
                !passwordEncoder.matches(
                        request.password(), user.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message",
                            "Invalid email or password"));
        }

        String token = jwtService.generateToken(user.getEmail());

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );

        return ResponseEntity.ok(response);
    }
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;
    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(
            @Valid @RequestBody LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        boolean passwordMatches =
                request.password() != null
                        && adminPassword != null
                        && MessageDigest.isEqual(
                        request.password().getBytes(StandardCharsets.UTF_8),
                        adminPassword.getBytes(StandardCharsets.UTF_8));

        if (!email.equalsIgnoreCase(adminEmail.trim())
                || !passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid admin email or password"));
        }

        String token = jwtService.generateAdminToken(adminEmail.trim());

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                null,
                "Administrator",
                adminEmail.trim()
        );

        return ResponseEntity.ok(response);
    }
}

