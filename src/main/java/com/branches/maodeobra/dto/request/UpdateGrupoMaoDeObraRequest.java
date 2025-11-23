package com.branches.maodeobra.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateGrupoMaoDeObraRequest(
        @NotBlank(message = "O campo 'descricao' é obrigatório") String descricao
) {
}

