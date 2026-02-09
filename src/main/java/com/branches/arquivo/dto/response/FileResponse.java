package com.branches.arquivo.dto.response;

import com.branches.arquivo.domain.ArquivoEntity;

public record FileResponse(
        Long id,
        String nomeArquivo,
        String descricao,
        String url
) {
    public static FileResponse from(ArquivoEntity arquivo) {
        return new FileResponse(
                arquivo.getId(),
                arquivo.getNomeArquivo(),
                arquivo.getDescricao(),
                arquivo.getUrl()
        );
    }
}
