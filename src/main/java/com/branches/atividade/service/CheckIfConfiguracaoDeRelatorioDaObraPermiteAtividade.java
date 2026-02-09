package com.branches.atividade.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdAndTenantIdService;
import com.branches.relatorio.domain.RelatorioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckIfConfiguracaoDeRelatorioDaObraPermiteAtividade {
    private final GetObraByIdAndTenantIdService getObraByIdAndTenantIdService;

    public void execute(RelatorioEntity relatorio, Long tenantId) {
        ObraEntity obra = getObraByIdAndTenantIdService.execute(relatorio.getObraId(), tenantId);

        if (obra.getConfiguracaoRelatorios().getShowAtividades()) return;

        throw new ForbiddenException();
    }

    public void execute(ObraEntity obra) {
        if (obra.getConfiguracaoRelatorios().getShowAtividades()) return;

        throw new ForbiddenException();
    }
}
