package com.branches.maodeobra.dto.response;

import com.branches.maodeobra.domain.enums.PresencaMaoDeObra;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;

import java.time.LocalTime;

public record MaoDeObraDeRelatorioResponse(
        Long id,
        Long maoDeObraId,
        String nome,
        String funcao,
        PresencaMaoDeObra presenca,
        LocalTime horaInicio,
        LocalTime horaFim,
        Integer minutosIntervalo,
        LocalTime horasTrabalhadas
) {
    public static MaoDeObraDeRelatorioResponse from(MaoDeObraDeRelatorioEntity entity) {
        return new MaoDeObraDeRelatorioResponse(
                entity.getId(),
                entity.getMaoDeObra().getId(),
                entity.getMaoDeObra().getNome(),
                entity.getFuncao(),
                entity.getPresenca(),
                entity.getHoraInicio(),
                entity.getHoraFim(),
                entity.getMinutosIntervalo(),
                entity.getHorasTrabalhadas()
        );

    }
}
