package com.branches.equipamento.dto.response;

import com.branches.equipamento.domain.EquipamentoEntity;

public record CreateEquipamentoResponse(
        Long id,
        String descricao
) {
    public static CreateEquipamentoResponse from(EquipamentoEntity saved) {
        return new CreateEquipamentoResponse(
                saved.getId(),
                saved.getDescricao()
        );
    }
}
