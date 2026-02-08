package com.branches.material.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateMaterialRequest(
        @NotBlank(message = "O campo 'descricao' é obrigatório")
        String descricao,
        String unidadeMedida
) {
}
