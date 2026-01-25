package com.branches.material.dto.response;

import com.branches.material.repository.projections.ItemTopMateriaisProjection;

public record GetItemTopMateriaisResponse(
        Long id,
        String descricao,
        Long quantidadeUso
) {
    public static GetItemTopMateriaisResponse from(ItemTopMateriaisProjection itemTopMateriaisProjection) {
        return new GetItemTopMateriaisResponse(
                itemTopMateriaisProjection.getId(),
                itemTopMateriaisProjection.getDescricao(),
                itemTopMateriaisProjection.getQuantidadeUso()
        );
    }
}
