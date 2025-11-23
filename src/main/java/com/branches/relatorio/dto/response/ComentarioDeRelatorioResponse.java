package com.branches.relatorio.dto.response;

import com.branches.relatorio.domain.ComentarioDeRelatorioEntity;

import java.util.List;

public record ComentarioDeRelatorioResponse(
        Long id,
        String descricao,
        List<CampoPersonalizadoResponse> camposPersonalizados
) {
    public static ComentarioDeRelatorioResponse from(ComentarioDeRelatorioEntity comentarioDeRelatorioEntity) {
        var camposPersonalizadosResponse = comentarioDeRelatorioEntity.getCamposPersonalizados() != null ?
                comentarioDeRelatorioEntity.getCamposPersonalizados().stream()
                        .map(CampoPersonalizadoResponse::from)
                        .toList() : null;

        return new ComentarioDeRelatorioResponse(
                comentarioDeRelatorioEntity.getId(),
                comentarioDeRelatorioEntity.getDescricao(),
                camposPersonalizadosResponse
        );
    }
}
