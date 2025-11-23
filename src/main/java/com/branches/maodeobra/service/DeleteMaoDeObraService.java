package com.branches.maodeobra.service;

import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.repository.MaoDeObraRepository;
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
    private final CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    public void execute(Long id, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaoDeObraService.execute(currentUserTenant);

        MaoDeObraEntity maoDeObraEntity = getMaoDeObraByIdAndTenantIdService.execute(id, tenantId);
        maoDeObraEntity.desativar();

        maoDeObraRepository.save(maoDeObraEntity);
    }
}
