package com.branches.maodeobra.service;

import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.maodeobra.repository.MaoDeObraDeRelatorioRepository;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GenerateRelatorioFileToUsersService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.ItemRelatorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeleteMaoDeObraDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService;
    private final CheckIfUserCanViewMaoDeObraService checkIfUserCanViewMaoDeObraService;
    private final GetMaoDeObraDeRelatorioByIdAndRelatorioId getMaoDeObraDeRelatorioByIdAndRelatorioId;
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final GenerateRelatorioFileToUsersService generateRelatorioFileToUsersService;

    public void execute(Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService.execute(relatorio.getObraId(), tenantId);

        checkIfUserCanViewMaoDeObraService.execute(userTenant);

        MaoDeObraDeRelatorioEntity maoDeObra = getMaoDeObraDeRelatorioByIdAndRelatorioId.execute(id, relatorio.getId());

        maoDeObraDeRelatorioRepository.delete(maoDeObra);

        generateRelatorioFileToUsersService.executeOnlyToNecessaryUsers(relatorio.getId(), ItemRelatorio.MAO_DE_OBRA);
    }
}
