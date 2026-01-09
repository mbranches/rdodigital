package com.branches.maodeobra.dto.response;

import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MaoDeObraResponse(
    Long id,
    String funcao,
    GrupoMaoDeObraResponse grupo,
    String nome,
    LocalTime horaInicio,
    LocalTime horaFim,
    Integer minutosIntervalo,
    LocalTime horasTrabalhadas
) {
    public static MaoDeObraResponse from(MaoDeObraEntity saved) {
        return new MaoDeObraResponse(
            saved.getId(),
            saved.getFuncao(),
            GrupoMaoDeObraResponse.from(saved.getGrupo()),
            saved.getNome(),
            saved.getHoraInicio(),
            saved.getHoraFim(),
            saved.getMinutosIntervalo(),
            saved.getHorasTrabalhadas()
        );
    }
}