package com.branches.condicaoclimatica.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateCondicaoClimaticaDeRelatorioRequest(
        @Valid
        @NotNull(message = "O campo 'condicaoClimaticaManha' é obrigatório")
        CondicaoClimaticaRequest condicaoClimaticaManha,
        @Valid
        @NotNull(message = "O campo 'condicaoClimaticaTarde' é obrigatório")
        CondicaoClimaticaRequest condicaoClimaticaTarde,
        @Valid
        @NotNull(message = "O campo 'condicaoClimaticaNoite' é obrigatório")
        CondicaoClimaticaRequest condicaoClimaticaNoite,
        @DecimalMax(value = "999.99", message = "O campo 'indicePluviometrico' deve ser menor ou igual a 999.99")
        @DecimalMin(value = "0.00", message = "O campo 'indicePluviometrico' deve ser maior ou igual a 0.00")
        BigDecimal indicePluviometrico
) {
}
