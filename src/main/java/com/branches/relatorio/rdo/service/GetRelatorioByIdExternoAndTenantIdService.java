package com.branches.relatorio.rdo.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.RelatorioEntity;
import com.branches.relatorio.rdo.repository.RelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetRelatorioByIdExternoAndTenantIdService {
    private final RelatorioRepository relatorioRepository;

    public RelatorioEntity execute(String relatorioExternalId, Long tenantId) {
        return relatorioRepository.findByIdExternoAndTenantIdAndAtivoIsTrue(relatorioExternalId, tenantId)
                .orElseThrow(() -> new NotFoundException("Relatório não encontrado com o id externo: " + relatorioExternalId + " para o tenant informado"));
    }
}
