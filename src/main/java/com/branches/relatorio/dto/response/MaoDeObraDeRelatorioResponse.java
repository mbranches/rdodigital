package com.branches.relatorio.dto.response;

import com.branches.maodeobra.domain.enums.PresencaMaoDeObra;
import com.branches.relatorio.domain.MaoDeObraDeRelatorioEntity;

import java.time.LocalTime;

public record MaoDeObraDeRelatorioResponse(
        Long id,
        Long maoDeObraId,
        String funcao,
        PresencaMaoDeObra presenca,
        LocalTime horaInicio,
        LocalTime horaFim,
        LocalTime horasIntervalo,
        LocalTime horasTrabalhadas
) {
    public static MaoDeObraDeRelatorioResponse from(MaoDeObraDeRelatorioEntity entity) {
        return new MaoDeObraDeRelatorioResponse(
                entity.getId(),
                entity.getMaoDeObra().getId(),
                entity.getFuncao(),
                entity.getPresenca(),
                entity.getHoraInicio(),
                entity.getHoraFim(),
                entity.getHorasIntervalo(),
                entity.getHorasTrabalhadas()
        );

    }
}
