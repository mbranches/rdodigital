package com.branches.relatorio.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusRelatorio {
    ANDAMENTO("Em Andamento"),
    REVISAO("Em Revisão"),
    APROVADO("Aprovado");

    private final String descricao;
}
