package com.branches.relatorio.dto.response;

import com.branches.relatorio.domain.MaoDeObraDeAtividadeDeRelatorioEntity;

public record MaoDeObraDeAtividadeResponse(
        Long id,
        Long maoDeObraId,
        String nome,
        String funcao
) {
    public static MaoDeObraDeAtividadeResponse from(MaoDeObraDeAtividadeDeRelatorioEntity maoDeObraDeAtividadeDeRelatorioEntity) {
        return new MaoDeObraDeAtividadeResponse(
                maoDeObraDeAtividadeDeRelatorioEntity.getId(),
                maoDeObraDeAtividadeDeRelatorioEntity.getMaoDeObra().getId(),
                maoDeObraDeAtividadeDeRelatorioEntity.getMaoDeObra().getNome(),
                maoDeObraDeAtividadeDeRelatorioEntity.getFuncao()
        );
    }
}
