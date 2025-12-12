package com.branches.obra.controller;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.response.UserOfObraResponse;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.repository.UserTenantRepository;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListAllObraUsersService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final UserTenantRepository userTenantRepository;

    public List<UserOfObraResponse> execute(String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());

        List<UserTenantEntity> obraUserTenants = userTenantRepository.findAllWithAccessToObra(obra.getId());

        List<UserEntity> users = obraUserTenants.stream()
                .map(UserTenantEntity::getUser)
                .toList();

        return users.stream()
                .map(UserOfObraResponse::from)
                .toList();
    }
}
