package com.branches.relatorio.dto.response;

import com.branches.relatorio.domain.MaterialDeRelatorioEntity;
import com.branches.relatorio.domain.enums.TipoMaterial;

public record MaterialDeRelatorioResponse(
         Long id,
         String descricao,
         String quantidade,
         TipoMaterial tipoMaterial
) {
    public static MaterialDeRelatorioResponse from(MaterialDeRelatorioEntity entity) {
        return new MaterialDeRelatorioResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getQuantidade(),
                entity.getTipoMaterial()
        );
    }
}
