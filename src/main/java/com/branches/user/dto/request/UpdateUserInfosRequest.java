package com.branches.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserInfosRequest(
        @NotBlank(message = "O campo 'nome' é obrigatório")
        String nome,
        String password
) {
}
