package com.branches.plano.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreatePlanoCheckoutRequest(
        @NotNull(message = "O ID do plano é obrigatório")
        Long planoId
) {
}
