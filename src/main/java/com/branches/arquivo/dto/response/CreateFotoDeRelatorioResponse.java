package com.branches.arquivo.dto.response;

import com.branches.arquivo.domain.ArquivoEntity;

public record CreateFotoDeRelatorioResponse(
        Long id,
        String url,
        String descricao
) {
    public static CreateFotoDeRelatorioResponse from(ArquivoEntity arquivo) {
        return new CreateFotoDeRelatorioResponse(
                arquivo.getId(),
                arquivo.getUrl(),
                arquivo.getDescricao()
        );
    }
}
