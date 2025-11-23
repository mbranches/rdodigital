package com.branches.ocorrencia.dto.response;

import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;

public record CreateTipoDeOcorrenciaResponse(
        Long id,
        String descricao
) {
    public static CreateTipoDeOcorrenciaResponse from(TipoDeOcorrenciaEntity saved) {
        return new CreateTipoDeOcorrenciaResponse(
                saved.getId(),
                saved.getDescricao()
        );
    }
}
