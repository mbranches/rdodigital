package com.branches.relatorio.rdo.dto.response;

import com.branches.relatorio.rdo.domain.MaterialDeRelatorioEntity;
import com.branches.relatorio.rdo.domain.enums.TipoMaterial;

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
