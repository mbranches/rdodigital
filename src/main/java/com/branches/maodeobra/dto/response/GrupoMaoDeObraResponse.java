package com.branches.maodeobra.dto.response;

import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;

public record GrupoMaoDeObraResponse(Long id, String descricao) {
    public static GrupoMaoDeObraResponse from(GrupoMaoDeObraEntity grupo) {
        return new GrupoMaoDeObraResponse(grupo.getId(), grupo.getDescricao());
    }
}


