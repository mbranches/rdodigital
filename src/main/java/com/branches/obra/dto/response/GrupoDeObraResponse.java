package com.branches.obra.dto.response;

import com.branches.obra.domain.GrupoDeObraEntity;

public record GrupoDeObraResponse(Long id, String descricao) {
    public static GrupoDeObraResponse from(GrupoDeObraEntity grupo) {
        return new GrupoDeObraResponse(grupo.getId(), grupo.getDescricao());
    }
}
