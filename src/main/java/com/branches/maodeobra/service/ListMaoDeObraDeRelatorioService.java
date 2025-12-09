package com.branches.maodeobra.service;

import com.branches.maodeobra.dto.response.MaoDeObraDeRelatorioResponse;
import com.branches.maodeobra.repository.MaoDeObraDeRelatorioRepository;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListMaoDeObraDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService;
    private final CheckIfUserCanViewMaoDeObraService checkIfUserCanViewMaoDeObraService;
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public List<MaoDeObraDeRelatorioResponse> execute(String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewMaoDeObraService.execute(userTenant);

        return maoDeObraDeRelatorioRepository.findAllByRelatorioId(relatorio.getId()).stream()
                .map(MaoDeObraDeRelatorioResponse::from)
                .toList();
    }
}
