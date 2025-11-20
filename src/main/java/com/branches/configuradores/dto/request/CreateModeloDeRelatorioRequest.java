package com.branches.configuradores.dto.request;

import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;

public record CreateModeloDeRelatorioRequest(
        String titulo,
        RecorrenciaRelatorio recorrenciaRelatorio
) {
}
