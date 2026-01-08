package com.branches.plano.dto.response;

import com.branches.plano.domain.PeriodoTesteEntity;

import java.time.LocalDateTime;

public record PeriodoDeTesteResponse(
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {
    public static PeriodoDeTesteResponse from(PeriodoTesteEntity entity) {
        return new PeriodoDeTesteResponse(
                entity.getDataInicio(),
                entity.getDataFim()
        );
    }
}
