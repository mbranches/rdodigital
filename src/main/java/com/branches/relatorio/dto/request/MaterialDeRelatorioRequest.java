package com.branches.relatorio.dto.request;

import com.branches.relatorio.domain.enums.TipoMaterial;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MaterialDeRelatorioRequest(
        Long id,
        @NotBlank(message = "O campo 'descricao' é obrigatório")
        String descricao,
        String quantidade,
        @NotNull(message = "O campo 'tipoMaterial' é obrigatório")
        TipoMaterial tipoMaterial
) {
}
