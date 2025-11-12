package com.branches.relatorio.maodeobra.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record UpdateMaoDeObraRequest (
    @NotBlank(message = "O campo 'funcao' é obrigatório") String funcao,
    @NotNull(message = "O campo 'grupoId' é obrigatório") Long grupoId,
    String nome,
    LocalTime horaInicio,
    LocalTime horaFim,
    LocalTime horasIntervalo
) {}
