package com.branches.material.dto.request;

import java.math.BigDecimal;

public record UpdateMaterialDeRelatorioRequest(
        BigDecimal quantidade,
        String unidadeMedida
) {
}
