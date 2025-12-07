package com.branches.relatorio.dto.request;

import com.branches.relatorio.domain.enums.StatusRelatorio;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateRelatorioRequest(
        @NotNull(message = "O campo 'numeroRelatorio' é obrigatório")
        @Min(value = 1, message = "O campo 'numeroRelatorio' deve ser maior que zero")
        Long numeroRelatorio,

        @NotNull(message = "O campo 'dataInicio' é obrigatório")
        LocalDate dataInicio,
        LocalDate dataFim,

        LocalTime horaInicioTrabalhos,
        LocalTime horaFimTrabalhos,
        LocalTime horasIntervalo,
        LocalTime horasTrabalhadas,

        @NotNull(message = "O campo 'prazoContratual' é obrigatório")
        Long prazoContratual,
        @NotNull(message = "O campo 'prazoDecorrido' é obrigatório")
        Long prazoDecorrido,
        @NotNull(message = "O campo 'prazoPraVencer' é obrigatório")
        Long prazoPraVencer,

        @NotNull(message = "O campo 'status' é obrigatório")
        StatusRelatorio status
) {
}
