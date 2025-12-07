package com.branches.arquivo.dto.response;

import com.branches.arquivo.domain.ArquivoEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArquivoResponse(
        Long id,
        String nomeArquivo,
        String descricao,
        String url
) {
    public static ArquivoResponse from(ArquivoEntity arquivoEntity) {
        return new ArquivoResponse(
                arquivoEntity.getId(),
                arquivoEntity.getNomeArquivo(),
                arquivoEntity.getDescricao(),
                arquivoEntity.getUrl()
        );
    }
}
