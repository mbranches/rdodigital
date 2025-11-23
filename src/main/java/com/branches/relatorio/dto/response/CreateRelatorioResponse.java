package com.branches.relatorio.dto.response;

import com.branches.relatorio.domain.RelatorioEntity;

public record CreateRelatorioResponse(
        String id
) {
    public static CreateRelatorioResponse from(RelatorioEntity relatorio) {
        return new CreateRelatorioResponse(relatorio.getIdExterno());
    }
}
