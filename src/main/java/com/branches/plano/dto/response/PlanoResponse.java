package com.branches.plano.dto.response;

import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.domain.enums.RecorrenciaPlano;

import java.math.BigDecimal;

public record PlanoResponse(
        Long id,
        String nome,
        String descricao,
        RecorrenciaPlano recorrenciaPlano,
        BigDecimal valor,
        Integer limiteUsuarios,
        Integer limiteObras) {
    public static PlanoResponse from(PlanoEntity plano) {
        return new PlanoResponse(
                plano.getId(),
                plano.getNome(),
                plano.getDescricao(),
                plano.getRecorrencia(),
                plano.getValor(),
                plano.getLimiteUsuarios(),
                plano.getLimiteObras()
        );
    }
}
