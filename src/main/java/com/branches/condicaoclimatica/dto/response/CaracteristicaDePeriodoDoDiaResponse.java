package com.branches.condicaoclimatica.dto.response;

import com.branches.condicaoclimatica.domain.CondicaoClimaticaEntity;
import com.branches.relatorio.domain.enums.Clima;
import com.branches.relatorio.domain.enums.CondicaoDoTempo;

public record CaracteristicaDePeriodoDoDiaResponse(
        Long id,
        Clima clima,
        CondicaoDoTempo condicaoDoTempo,
        Boolean ativo
) {
    public static CaracteristicaDePeriodoDoDiaResponse from(CondicaoClimaticaEntity caracteristicaManha) {
        return new CaracteristicaDePeriodoDoDiaResponse(
                caracteristicaManha.getId(),
                caracteristicaManha.getClima(),
                caracteristicaManha.getCondicaoDoTempo(),
                caracteristicaManha.getAtivo()
        );
    }
}
