package com.branches.maodeobra.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateMaoDeObraDeAtividadeRequest(
        Long id,
        @NotNull(message = "O campo 'maoDeObraId' é obrigatório")
        Long maoDeObraId
) {
}
