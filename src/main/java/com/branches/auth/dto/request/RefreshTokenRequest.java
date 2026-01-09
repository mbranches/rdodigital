package com.branches.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "O campo 'refreshToken' é obrigatório") String refreshToken
) {
}
