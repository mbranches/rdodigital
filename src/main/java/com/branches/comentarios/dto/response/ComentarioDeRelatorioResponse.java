package com.branches.comentarios.dto.response;

import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.relatorio.dto.response.CampoPersonalizadoResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ComentarioDeRelatorioResponse(
        Long id,
        String descricao,
        AutorResponse autor,
        LocalDateTime dataCriacao,
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
                AutorResponse.from(comentarioDeRelatorioEntity.getAutor()),
                comentarioDeRelatorioEntity.getDataCriacao(),
                camposPersonalizadosResponse
        );
    }
}
