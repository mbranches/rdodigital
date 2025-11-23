package com.branches.material.dto.request;

import com.branches.material.domain.enums.TipoMaterial;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMaterialDeRelatorioRequest(
        @NotBlank(message = "O campo 'descricao' é obrigatório")
        String descricao,
        String quantidade,
        @NotNull(message = "O campo 'tipoMaterial' é obrigatório")
        TipoMaterial tipoMaterial
) {
}
