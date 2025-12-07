package com.branches.utils;

import com.branches.arquivo.domain.enums.TipoArquivo;

public enum ItemRelatorio {
    FOTOS,
    VIDEOS,
    ATIVIDADES,
    COMENTARIOS,
    CONDICAO_CLIMATICA,
    EQUIPAMENTOS,
    MAO_DE_OBRA,
    MATERIAIS,
    OCORRENCIAS,
    HORARIO_DE_TRABALHO;

    public static ItemRelatorio fromTipoArquivo(TipoArquivo tipoArquivo) {
        return switch (tipoArquivo) { //todo: quando implementar anexos adicionar aqui
            case FOTO -> FOTOS;
            case VIDEO -> VIDEOS;
            default -> throw new IllegalArgumentException("Tipo de arquivo não mapeado para ItemRelatorio: " + tipoArquivo);
        };
    }
}
