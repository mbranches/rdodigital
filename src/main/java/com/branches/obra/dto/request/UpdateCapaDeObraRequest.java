package com.branches.obra.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCapaDeObraRequest(
        @NotBlank(message = "O campo 'base64Image' é obrigatório")
        String base64Image
) {
}
