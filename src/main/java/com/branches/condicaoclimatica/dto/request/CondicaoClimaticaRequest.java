package com.branches.condicaoclimatica.dto.request;

import com.branches.condicaoclimatica.domain.enums.Clima;
import com.branches.condicaoclimatica.domain.enums.CondicaoDoTempo;
import jakarta.validation.constraints.NotNull;

public record CondicaoClimaticaRequest(
        @NotNull(message = "O campo 'clima' é obrigatório")
        Clima clima,
        @NotNull(message = "O campo 'condicaoDoTempo' é obrigatório")
        CondicaoDoTempo condicaoDoTempo,
        @NotNull(message = "O campo 'ativo' é obrigatório")
        Boolean ativo
) {}
