package com.branches.relatorio.rdo.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateEquipamentoDeRelatorioRequest(
        @NotNull(message = "O campo 'equipamentoId' é obrigatório")
        Long equipamentoId,
        @NotNull(message = "O campo 'quantidade' é obrigatório")
        Integer quantidade
) {
}
