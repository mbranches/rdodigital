package com.branches.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserFotoDePerfilRequest(
        @NotBlank(message = "O campo 'base64Image' é obrigatório")
        String base64Image
) {
}
