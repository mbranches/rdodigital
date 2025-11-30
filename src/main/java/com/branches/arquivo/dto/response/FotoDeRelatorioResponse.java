package com.branches.arquivo.dto.response;

import com.branches.arquivo.domain.ArquivoEntity;

public record FotoDeRelatorioResponse(
        Long id,
        String url,
        String descricao
) {
    public static FotoDeRelatorioResponse from(ArquivoEntity arquivo) {
        return new FotoDeRelatorioResponse(
                arquivo.getId(),
                arquivo.getUrl(),
                arquivo.getDescricao()
        );
    }
}
