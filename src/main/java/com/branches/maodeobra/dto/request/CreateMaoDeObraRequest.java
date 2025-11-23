package com.branches.maodeobra.dto.request;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record CreateMaoDeObraRequest(
    @NotBlank(message = "O campo 'funcao' é obrigatório") String funcao,
    @NotNull(message = "O campo 'grupoId' é obrigatório") Long grupoId,
    String nome,
    LocalTime horaInicio,
    LocalTime horaFim,
    LocalTime horasIntervalo,
    @NotNull(message = "O campo 'tipo' é obrigatório") TipoMaoDeObra tipo
) {}