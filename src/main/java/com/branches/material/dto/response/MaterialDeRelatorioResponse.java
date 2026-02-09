package com.branches.material.dto.response;

import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.material.domain.enums.TipoMaterial;

import java.math.BigDecimal;

public record MaterialDeRelatorioResponse(
         Long id,
         Long materialId,
         String descricao,
         String unidadeMedida,
         BigDecimal quantidade,
         TipoMaterial tipoMaterial
) {
    public static MaterialDeRelatorioResponse from(MaterialDeRelatorioEntity entity) {
        return new MaterialDeRelatorioResponse(
                entity.getId(),
                entity.getMaterial().getId(),
                entity.getMaterial().getDescricao(),
                entity.getUnidadeMedida(),
                entity.getQuantidade(),
                entity.getTipoMaterial()
        );
    }
}
