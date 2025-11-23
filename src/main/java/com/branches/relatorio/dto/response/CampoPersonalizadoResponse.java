package com.branches.relatorio.dto.response;

import com.branches.relatorio.domain.CampoPersonalizadoEntity;

public record CampoPersonalizadoResponse(
        String campo,
        String descricao
) {
    public static CampoPersonalizadoResponse from(CampoPersonalizadoEntity campoPersonalizadoEntity) {
        return new CampoPersonalizadoResponse(
                campoPersonalizadoEntity.getCampo(),
                campoPersonalizadoEntity.getDescricao()
        );
    }
}
