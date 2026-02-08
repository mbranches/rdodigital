package com.branches.material.service;

import com.branches.material.domain.MaterialDeRelatorioEntity;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.material.dto.request.UpdateMaterialDeRelatorioRequest;
import com.branches.material.repository.MaterialDeRelatorioRepository;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UpdateMaterialDeRelatorioService {

    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;
    private final GetMaterialDeRelatorioByIdAndRelatorioIdService getMaterialDeRelatorioByIdAndRelatorioIdService;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaterialService checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService;
    private final CheckIfUserCanViewMateriaisDeRelatorioService checkIfUserCanViewMateriaisDeRelatorioService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public void execute(UpdateMaterialDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewMateriaisDeRelatorioService.execute(userTenant);

        MaterialDeRelatorioEntity entity = getMaterialDeRelatorioByIdAndRelatorioIdService.execute(id, relatorio.getId());
        entity.setUnidadeMedida(request.unidadeMedida());
        entity.setQuantidade(request.quantidade());

        materialDeRelatorioRepository.save(entity);
    }
}
