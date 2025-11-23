package com.branches.equipamento.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateEquipamentoDeRelatorioRequest(
        @NotNull(message = "O campo 'equipamentoId' é obrigatório")
        Long equipamentoId,
        @NotNull(message = "O campo 'quantidade' é obrigatório")
        Integer quantidade
) {
}
