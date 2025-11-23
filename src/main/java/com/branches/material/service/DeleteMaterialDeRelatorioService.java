package com.branches.material.service;

import com.branches.material.domain.MaterialDeRelatorioEntity;
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
public class DeleteMaterialDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaterialService checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService;
    private final CheckIfUserCanViewMateriaisService checkIfUserCanViewMateriaisService;
    private final GetMaterialByIdAndRelatorioIdService getMaterialByIdAndRelatorioIdService;
    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;

    public void execute(Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService.execute(relatorio.getObraId(), tenantId);

        checkIfUserCanViewMateriaisService.execute(userTenant);

        MaterialDeRelatorioEntity material = getMaterialByIdAndRelatorioIdService.execute(id, relatorio.getId());

        materialDeRelatorioRepository.delete(material);
    }
}
