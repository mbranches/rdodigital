package com.branches.relatorio.service;

import com.branches.exception.ForbiddenException;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeleteRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final RelatorioRepository relatorioRepository;

    public void execute(String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserCanDeleteRelatorio(currentUserTenant);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);
        relatorio.setInativo();

        relatorioRepository.save(relatorio);
    }

    private void checkIfUserCanDeleteRelatorio(UserTenantEntity currentUserTenant) {
        if (currentUserTenant.getAuthorities().getRelatorios().getCanDelete()) return;

        throw new ForbiddenException();
    }

}
