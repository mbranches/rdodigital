package com.branches.relatorio.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateLogoDeConfigDeRelatorioRequest(
        @NotNull(message = "O campo 'logoBase64' é obrigatório")
        String base64Image,
        @NotNull(message = "O campo 'exibir' é obrigatório")
        Boolean exibir
) {
}
