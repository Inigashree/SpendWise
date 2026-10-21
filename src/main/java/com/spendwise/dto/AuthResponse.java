
package com.spendwise.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String fullName,
        String email
) {}

