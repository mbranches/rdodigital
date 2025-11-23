package com.branches.relatorio.dto.response;

import com.branches.relatorio.domain.EquipamentoDeRelatorioEntity;

public record EquipamentoDeRelatorioResponse(
        Long id,
        Long equipamentoId,
        String descricao,
        Integer quantidade
) {
    public static EquipamentoDeRelatorioResponse from(EquipamentoDeRelatorioEntity equipamentoDeRelatorioEntity) {
        return new EquipamentoDeRelatorioResponse(
                equipamentoDeRelatorioEntity.getId(),
                equipamentoDeRelatorioEntity.getEquipamento().getId(),
                equipamentoDeRelatorioEntity.getEquipamento().getDescricao(),
                equipamentoDeRelatorioEntity.getQuantidade()
        );
    }
}
