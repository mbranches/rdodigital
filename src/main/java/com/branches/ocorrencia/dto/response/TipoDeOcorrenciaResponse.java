package com.branches.ocorrencia.dto.response;

import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;

public record TipoDeOcorrenciaResponse(Long id, String descricao) {
    public static TipoDeOcorrenciaResponse from(TipoDeOcorrenciaEntity tipoDeOcorrencia) {
        return new TipoDeOcorrenciaResponse(tipoDeOcorrencia.getId(), tipoDeOcorrencia.getDescricao());
    }
}
