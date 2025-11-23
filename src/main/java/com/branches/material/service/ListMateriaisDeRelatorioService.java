package com.branches.material.service;

import com.branches.material.dto.response.MaterialDeRelatorioResponse;
import com.branches.material.repository.MaterialDeRelatorioRepository;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListMateriaisDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaterialService checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService;
    private final CheckIfUserCanViewMateriaisService checkIfUserCanViewMateriaisService;
    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;

    public List<MaterialDeRelatorioResponse> execute(String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService.execute(relatorio.getObraId(), tenantId);

        checkIfUserCanViewMateriaisService.execute(userTenant);

        return materialDeRelatorioRepository.findAllByRelatorioId(relatorio.getId()).stream()
                .map(MaterialDeRelatorioResponse::from)
                .toList();
    }
}
