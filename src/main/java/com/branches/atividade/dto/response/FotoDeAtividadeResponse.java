package com.branches.atividade.dto.response;

import com.branches.atividade.domain.FotoDeAtividadeEntity;

public record FotoDeAtividadeResponse(
    Long id,
    String url
) {
    public static FotoDeAtividadeResponse from(FotoDeAtividadeEntity entity) {
        return new FotoDeAtividadeResponse(
            entity.getId(),
            entity.getUrl()
        );
    }
}
