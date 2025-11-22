package com.branches.relatorio.rdo.dto.request;

import com.branches.relatorio.maodeobra.domain.enums.PresencaMaoDeObra;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record UpdateMaoDeObraDeRelatorioRequest(
    @NotNull(message = "O campo 'maoDeObraId' é obrigatório")
    Long maoDeObraId,
    @NotNull(message = "O campo 'presenca' é obrigatório")
    PresencaMaoDeObra presenca,
    LocalTime horaInicio,
    LocalTime horaFim,
    LocalTime horasIntervalo
) {
}
