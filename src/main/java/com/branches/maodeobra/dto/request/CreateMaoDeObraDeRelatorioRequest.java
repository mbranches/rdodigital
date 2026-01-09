package com.branches.maodeobra.dto.request;

import com.branches.maodeobra.domain.enums.PresencaMaoDeObra;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record CreateMaoDeObraDeRelatorioRequest(
        @NotNull(message = "O campo 'maoDeObraId' é obrigatório")
        Long maoDeObraId,
        @NotNull(message = "O campo 'presenca' é obrigatório")
        PresencaMaoDeObra presenca,
        LocalTime horaInicio,
        LocalTime horaFim,
        Integer minutosIntervalo
) {
}
