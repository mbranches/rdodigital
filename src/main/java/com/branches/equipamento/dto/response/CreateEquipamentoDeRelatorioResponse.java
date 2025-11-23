package com.branches.equipamento.dto.response;

import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;

public record CreateEquipamentoDeRelatorioResponse(
        Long id,
        Long equipamentoId,
        String descricao,
        Integer quantidade
) {
    public static CreateEquipamentoDeRelatorioResponse from(EquipamentoDeRelatorioEntity entity) {
        return new CreateEquipamentoDeRelatorioResponse(
                entity.getId(),
                entity.getEquipamento().getId(),
                entity.getEquipamento().getDescricao(),
                entity.getQuantidade()
        );
    }
}
