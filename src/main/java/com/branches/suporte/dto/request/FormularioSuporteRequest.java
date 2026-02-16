package com.branches.suporte.dto.request;

import com.branches.suporte.entity.enums.TipoSuporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FormularioSuporteRequest(
        @NotBlank(message = "O campo 'assunto' é obrigatório.")
        String assunto,
        @NotBlank(message = "O campo 'descricao' é obrigatório.")
        String descricao,
        @NotNull(message = "O campo 'tipoSuporte' é obrigatório.")
        TipoSuporte tipoSuporte
) {
}
