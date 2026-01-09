package com.branches.maodeobra.dto.request;

import com.branches.maodeobra.domain.enums.PresencaMaoDeObra;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record UpdateMaoDeObraDeRelatorioRequest(
    @NotNull(message = "O campo 'presenca' é obrigatório")
    PresencaMaoDeObra presenca,
    LocalTime horaInicio,
    LocalTime horaFim,
    Integer minutosIntervalo
) {
}
