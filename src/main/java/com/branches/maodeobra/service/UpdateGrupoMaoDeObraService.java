package com.branches.maodeobra.service;

import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.dto.request.UpdateGrupoMaoDeObraRequest;
import com.branches.maodeobra.repository.GrupoMaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateGrupoMaoDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetGrupoMaoDeObraByIdAndTenantIdService getGrupoMaoDeObraByIdAndTenantIdService;
    private final GrupoMaoDeObraRepository grupoMaoDeObraRepository;
    private final CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    public void execute(Long id, String tenantExternalId, UpdateGrupoMaoDeObraRequest request, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaoDeObraService.execute(currentUserTenant);

        GrupoMaoDeObraEntity grupoMaoDeObraEntity = getGrupoMaoDeObraByIdAndTenantIdService.execute(id, tenantId);

        grupoMaoDeObraEntity.setDescricao(request.descricao());

        grupoMaoDeObraRepository.save(grupoMaoDeObraEntity);
    }
}

