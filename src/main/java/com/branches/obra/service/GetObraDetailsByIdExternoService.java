package com.branches.obra.service;

import com.branches.arquivo.domain.ArquivoEntity;
import com.branches.arquivo.repository.ArquivoRepository;
import com.branches.exception.NotFoundException;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.obra.dto.response.GetObraDetailsByIdExternoResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.obra.repository.projections.ObraDetailsProjection;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetObraDetailsByIdExternoService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final RelatorioRepository relatorioRepository;
    private final ObraRepository obraRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final ArquivoRepository arquivoRepository;

    public GetObraDetailsByIdExternoResponse execute(String idExterno, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantDaObraId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantDaObraId);

        ObraDetailsProjection obra = obraRepository.findObraDetailsByIdExternoAndTenantId(idExterno, tenantDaObraId)
                .orElseThrow(() -> new NotFoundException("Obra não encontrada com o id: " + idExterno));

        List<RelatorioProjection> relatoriosRecentes = relatorioRepository.findTop5ByObraIdProjection(obra.getId());

        List<ArquivoEntity> fotosRecentes = arquivoRepository.findTop5FotosDeRelatoriosByObraId(obra.getId());

        checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());

        return GetObraDetailsByIdExternoResponse.from(obra, relatoriosRecentes, fotosRecentes);
    }
}
