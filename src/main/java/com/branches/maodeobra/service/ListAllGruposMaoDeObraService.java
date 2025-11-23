package com.branches.maodeobra.service;

import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.dto.response.GrupoMaoDeObraResponse;
import com.branches.maodeobra.repository.GrupoMaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListAllGruposMaoDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GrupoMaoDeObraRepository grupoMaoDeObraRepository;
    private final CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    public List<GrupoMaoDeObraResponse> execute(String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaoDeObraService.execute(currentUserTenant);

        List<GrupoMaoDeObraEntity> grupoMaoDeObraEntityList = grupoMaoDeObraRepository.findAllByTenantIdAndAtivoIsTrue(tenantId);

        return grupoMaoDeObraEntityList.stream()
                .map(GrupoMaoDeObraResponse::from)
                .toList();
    }
}
