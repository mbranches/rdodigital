package com.branches.maodeobra.service;

import com.branches.maodeobra.dto.request.CreateGrupoMaoDeObraRequest;
import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.dto.response.CreateGrupoMaoDeObraResponse;
import com.branches.maodeobra.repository.GrupoMaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateGrupoMaoDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GrupoMaoDeObraRepository grupoMaoDeObraRepository;
    private final CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    public CreateGrupoMaoDeObraResponse execute(String tenantExternalId, CreateGrupoMaoDeObraRequest request, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaoDeObraService.execute(currentUserTenant);

        GrupoMaoDeObraEntity grupoMaoDeObraEntity = GrupoMaoDeObraEntity.builder()
                .tenantId(tenantId)
                .descricao(request.descricao())
                .ativo(true)
                .build();

        GrupoMaoDeObraEntity saved = grupoMaoDeObraRepository.save(grupoMaoDeObraEntity);

        return CreateGrupoMaoDeObraResponse.from(saved);
    }
}

