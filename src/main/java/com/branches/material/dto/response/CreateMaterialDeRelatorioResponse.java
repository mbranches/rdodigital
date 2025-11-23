package com.branches.material.dto.response;

import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.material.domain.enums.TipoMaterial;

public record CreateMaterialDeRelatorioResponse(
        Long id,
        String descricao,
        String quantidade,
        TipoMaterial tipoMaterial
) {
    public static CreateMaterialDeRelatorioResponse from(MaterialDeRelatorioEntity entity) {
        return new CreateMaterialDeRelatorioResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getQuantidade(),
                entity.getTipoMaterial()
        );
    }
}
