package com.branches.material.dto.response;

import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.material.domain.enums.TipoMaterial;

public record MaterialDeRelatorioResponse(
         Long id,
         Long materialId,
         String descricao,
         String quantidade,
         TipoMaterial tipoMaterial
) {
    public static MaterialDeRelatorioResponse from(MaterialDeRelatorioEntity entity) {
        return new MaterialDeRelatorioResponse(
                entity.getId(),
                entity.getMaterial().getId(),
                entity.getMaterial().getDescricao(),
                entity.getQuantidade(),
                entity.getTipoMaterial()
        );
    }
}
