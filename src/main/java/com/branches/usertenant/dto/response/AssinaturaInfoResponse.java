package com.branches.usertenant.dto.response;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.plano.dto.response.PlanoResponse;

import java.time.LocalDate;

public record AssinaturaInfoResponse(
        PlanoResponse plano,
        LocalDate dataInicio,
        LocalDate dataFim
) {
    public static AssinaturaInfoResponse from(AssinaturaEntity assinatura) {
        return new AssinaturaInfoResponse(
                PlanoResponse.from(assinatura.getPlano()),
                assinatura.getDataInicio(),
                assinatura.getDataFim()
        );
    }
}
