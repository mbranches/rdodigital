package com.branches.relatorio.dto.response;

import com.branches.relatorio.domain.CaracteristicaDePeriodoDoDiaEntity;
import com.branches.relatorio.domain.enums.Clima;
import com.branches.relatorio.domain.enums.CondicaoDoTempo;

public record CaracteristicaDePeriodoDoDiaResponse(
        Long id,
        Clima clima,
        CondicaoDoTempo condicaoDoTempo,
        Boolean ativo
) {
    public static CaracteristicaDePeriodoDoDiaResponse from(CaracteristicaDePeriodoDoDiaEntity caracteristicaManha) {
        return new CaracteristicaDePeriodoDoDiaResponse(
                caracteristicaManha.getId(),
                caracteristicaManha.getClima(),
                caracteristicaManha.getCondicaoDoTempo(),
                caracteristicaManha.getAtivo()
        );
    }
}
