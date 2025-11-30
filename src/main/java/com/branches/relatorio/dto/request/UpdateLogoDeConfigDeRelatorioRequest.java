package com.branches.relatorio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateLogoDeConfigDeRelatorioRequest(
        @NotBlank(message = "O campo 'logoBase64' é obrigatório")
        String logoBase64,
        @NotNull(message = "O campo 'exibir' é obrigatório")
        Boolean exibir
) {
}
