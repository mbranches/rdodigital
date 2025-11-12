package com.branches.relatorio.maodeobra.service;

import com.branches.exception.ForbiddenException;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeleteMaoDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetMaoDeObraByIdAndTenantIdService getMaoDeObraByIdAndTenantIdService;
    private final MaoDeObraRepository maoDeObraRepository;

    public void execute(Long id, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaoDeObra(currentUserTenant);

        MaoDeObraEntity maoDeObraEntity = getMaoDeObraByIdAndTenantIdService.execute(id, tenantId);
        maoDeObraEntity.desativar();

        maoDeObraRepository.save(maoDeObraEntity);
    }

    private void checkIfUserHasAccessToMaoDeObra(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getAuthorities().getCadastros().getMaoDeObra()) {
            throw new ForbiddenException();
        }
    }
}
