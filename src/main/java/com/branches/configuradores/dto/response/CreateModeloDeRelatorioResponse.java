package com.branches.configuradores.dto.response;

import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;

public record CreateModeloDeRelatorioResponse(
        Long id,
        String tituloRelatorio,
        RecorrenciaRelatorio recorrenciaRelatorio,
        Boolean isDefault
) {
    public static CreateModeloDeRelatorioResponse from(ModeloDeRelatorioEntity saved) {
        return new CreateModeloDeRelatorioResponse(
                saved.getId(),
                saved.getTitulo(),
                saved.getRecorrenciaRelatorio(),
                saved.getIsDefault()
        );
    }
}
