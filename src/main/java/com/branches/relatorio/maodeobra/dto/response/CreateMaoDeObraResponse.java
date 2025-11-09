package com.branches.relatorio.maodeobra.dto.response;

import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateMaoDeObraResponse(
    Long id,
    String funcao,
    GrupoMaoDeObraResponse grupo,
    String nome,
    LocalTime horaInicio,
    LocalTime horaFim,
    LocalTime horasIntervalo,
    LocalTime horasTrabalhadas
) {
    public static CreateMaoDeObraResponse from(MaoDeObraEntity saved) {
        return new CreateMaoDeObraResponse(
            saved.getId(),
            saved.getFuncao(),
            saved.getGrupo() != null ? GrupoMaoDeObraResponse.from(saved.getGrupo()) : null,
            saved.getNome(),
            saved.getHoraInicio(),
            saved.getHoraFim(),
            saved.getHorasIntervalo(),
            saved.getHorasTrabalhadas()
        );
    }
}