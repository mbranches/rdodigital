package com.branches.plano.dto.request;

import com.branches.plano.domain.enums.RecorrenciaPlano;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePlanoRequest(
        @NotBlank(message = "O campo 'nome' é obrigatório")
        String nome,
        @NotBlank(message = "O campo 'descricao' é obrigatório")
        String descricao,
        @NotNull(message = "O campo 'valor' é obrigatório")
        BigDecimal valor,
        @NotNull(message = "O campo 'recorrencia' é obrigatório")
        RecorrenciaPlano recorrencia,
        @NotNull(message = "O campo 'limiteUsuarios' é obrigatório")
        Integer limiteUsuarios,
        @NotNull(message = "O campo 'limiteObras' é obrigatório")
        Integer limiteObras
) {
}
