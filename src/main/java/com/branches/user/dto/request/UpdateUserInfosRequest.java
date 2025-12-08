package com.branches.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserInfosRequest(
        @NotBlank(message = "O campo 'nome' é obrigatório")
        String nome,
        @NotBlank(message = "O campo 'cargo' é obrigatório")
        String cargo,
        String password
) {
}
