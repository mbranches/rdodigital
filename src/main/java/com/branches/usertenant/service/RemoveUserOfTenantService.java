package com.branches.usertenant.service;

import com.branches.exception.ForbiddenException;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.user.service.GetUserByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.UserTenantKey;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.repository.UserTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RemoveUserOfTenantService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetUserByIdExternoService getUserByIdExternoService;
    private final GetUserTenantByIdService getUserTenantByIdService;
    private final UserTenantRepository userTenantRepository;

    public void execute(String tenantExternalId, String userExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserCanRemoveUser(currentUserTenant);

        UserEntity userToRemove = getUserByIdExternoService.execute(userExternalId);

        UserTenantEntity userTenantToDelete = getUserTenantByIdService.execute(UserTenantKey.from(userToRemove.getId(), tenantId));

        userTenantRepository.delete(userTenantToDelete);
    }

    private void checkIfUserCanRemoveUser(UserTenantEntity currentUserTenant) {
        if (currentUserTenant.getPerfil().equals(PerfilUserTenant.ADMINISTRADOR)) return;

        throw new ForbiddenException();
    }
}
