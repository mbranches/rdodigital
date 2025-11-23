package com.branches.equipamento.dto.response;

import com.branches.equipamento.domain.EquipamentoEntity;

public record EquipamentoResponse(Long id, String descricao) {
    public static EquipamentoResponse from(EquipamentoEntity equipamento) {
        return new EquipamentoResponse(equipamento.getId(), equipamento.getDescricao());
    }
}
