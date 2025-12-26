package com.branches.material.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateMaterialRequest(
        @NotBlank(message = "O campo 'descricao' é obrigatório")
        String descricao
) {
}
