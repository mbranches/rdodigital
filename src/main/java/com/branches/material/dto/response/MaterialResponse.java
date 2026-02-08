package com.branches.material.dto.response;

import com.branches.material.domain.MaterialEntity;

public record MaterialResponse(
        Long id,
        String descricao,
        String unidadeMedida
) {
    public static MaterialResponse from(MaterialEntity material) {
        return new MaterialResponse(
                material.getId(),
                material.getDescricao(),
                material.getUnidadeMedida()
        );
    }
}
