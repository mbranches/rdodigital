package com.branches.material.dto.request;

import com.branches.material.domain.enums.TipoMaterial;
import jakarta.validation.constraints.NotNull;

public record CreateMaterialDeRelatorioRequest(
        @NotNull(message = "O campo 'materialId' é obrigatório")
        Long materialId,
        String quantidade,
        @NotNull(message = "O campo 'tipoMaterial' é obrigatório")
        TipoMaterial tipoMaterial
) {
}
