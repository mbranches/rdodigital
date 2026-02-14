package com.branches.obra.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.repository.ObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeleteObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final ObraRepository obraRepository;
    private final GetCurrentUserTenantService getCurrenUserTenantService;

    public void execute(String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrenUserTenantService.execute(userTenants, tenantId);

        ObraEntity obraToDelete = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserCanDeleteObra(obraToDelete.getId(), currentUserTenant);

        obraToDelete.setAtivo(false);

        obraRepository.save(obraToDelete);
    }

    private void checkIfUserCanDeleteObra(Long id, UserTenantEntity userTenant) {
        if (!(userTenant.getAuthorities().getObras().getCanDelete() && (userTenant.isAdministrador() || userTenant.getObrasPermitidasIds().contains(id)))) {
            throw new ForbiddenException();
        }
    }
}