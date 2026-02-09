package com.branches.atividade.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateFotoDeAtividadeRequest(
        @NotBlank(message = "O campo 'base64Image' é obrigatório")
        String base64Image,
        @NotBlank(message = "O campo 'fileName' é obrigatório")
        String fileName
) {
}
