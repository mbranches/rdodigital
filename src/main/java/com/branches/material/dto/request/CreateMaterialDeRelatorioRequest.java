package com.branches.material.dto.request;

import com.branches.material.domain.enums.TipoMaterial;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateMaterialDeRelatorioRequest(
        @NotNull(message = "O campo 'materialId' é obrigatório")
        Long materialId,
        String unidadeMedida,
        BigDecimal quantidade,
        @NotNull(message = "O campo 'tipoMaterial' é obrigatório")
        TipoMaterial tipoMaterial
) {
}
