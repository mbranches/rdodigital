package com.branches.material.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateMaterialDeRelatorioRequest(
        @NotBlank(message = "O campo 'quantidade' é obrigatório")
        String quantidade
) {
}
