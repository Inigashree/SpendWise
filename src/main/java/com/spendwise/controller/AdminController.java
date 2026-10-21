
package com.spendwise.controller;

import com.spendwise.entity.User;
import com.spendwise.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Locale;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return Map.of(
                "totalUsers", userRepository.count()
        );
    }

    @GetMapping("/users")
    public Map<String, Object> getUsers(
            @RequestParam(defaultValue = "") String search) {

        String term = search.trim().toLowerCase(Locale.ROOT);

        List<Map<String, Object>> users =
                userRepository.findAll().stream()
                        .filter(user ->
                                term.isEmpty()
                                        || safe(user.getFullName()).contains(term)
                                        || safe(user.getEmail()).contains(term)
                        )
                        .map(user -> Map.<String, Object>of(
                                "id", user.getId(),
                                "fullName", user.getFullName(),
                                "email", user.getEmail()
                        ))
                        .toList();

        return Map.of(
                "totalUsers", userRepository.count(),
                "filteredUsers", users.size(),
                "users", users
        );
    }

    private String safe(String value) {
        return value == null
                ? ""
                : value.toLowerCase(Locale.ROOT);
    }
}
