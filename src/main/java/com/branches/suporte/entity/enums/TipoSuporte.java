package com.branches.suporte.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TipoSuporte {
    REPORTE_DE_PROBLEMA("Reporte de Problema"),
    SUGESTAO_DE_MELHORIA("Sugestão de Melhoria"),
    DUVIDA("Dúvida"),
    CANCELAMENTO("Cancelamento"),
    OUTRO("Outro");

    private final String descricao;
}
