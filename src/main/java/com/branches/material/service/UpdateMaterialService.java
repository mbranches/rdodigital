package com.branches.material.service;

import com.branches.material.domain.MaterialEntity;
import com.branches.material.dto.request.UpdateMaterialRequest;
import com.branches.material.repository.MaterialRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateMaterialService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToMaterialService checkIfUserHasAccessToMaterialService;
    private final GetMaterialByIdAndTenantIdService getMaterialByIdAndTenantIdService;
    private final MaterialRepository materialRepository;

    public void execute(Long id, UpdateMaterialRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaterialService.execute(currentUserTenant);

        MaterialEntity materialEntity = getMaterialByIdAndTenantIdService.execute(id, tenantId);
        materialEntity.setDescricao(request.descricao());

        materialRepository.save(materialEntity);
    }
}
