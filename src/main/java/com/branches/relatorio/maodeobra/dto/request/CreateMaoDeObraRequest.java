package com.branches.relatorio.maodeobra.dto.request;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import com.branches.relatorio.maodeobra.validation.ValidMaoDeObraRequest;

@ValidMaoDeObraRequest
public record CreateMaoDeObraRequest(
    @NotBlank(message = "O campo 'funcao' é obrigatório") String funcao,
    Long grupoId,
    String nome,
    LocalTime horaInicio,
    LocalTime horaFim,
    LocalTime horasIntervalo,
    @NotNull(message = "O campo 'tipo' é obrigatório") TipoMaoDeObra tipo
) {}