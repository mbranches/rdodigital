package com.branches.maodeobra.dto.response;

import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.maodeobra.domain.enums.PresencaMaoDeObra;

import java.time.LocalTime;

public record CreateMaoDeObraDeRelatorioResponse(
        Long id,
        Long maoDeObraId,
        String funcao,
        PresencaMaoDeObra presenca,
        LocalTime horaInicio,
        LocalTime horaFim,
        Integer minutosIntervalo,
        LocalTime horasTrabalhadas
) {
    public static CreateMaoDeObraDeRelatorioResponse from(MaoDeObraDeRelatorioEntity entity) {
        return new CreateMaoDeObraDeRelatorioResponse(
                entity.getId(),
                entity.getMaoDeObra().getId(),
                entity.getFuncao(),
                entity.getPresenca(),
                entity.getHoraInicio(),
                entity.getHoraFim(),
                entity.getMinutosIntervalo(),
                entity.getHorasTrabalhadas()
        );

    }
}
