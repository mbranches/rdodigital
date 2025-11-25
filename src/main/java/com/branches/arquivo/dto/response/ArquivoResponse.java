package com.branches.arquivo.dto.response;

import com.branches.arquivo.domain.ArquivoEntity;

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
