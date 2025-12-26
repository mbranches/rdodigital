package com.branches.material.service;

import com.branches.material.domain.MaterialEntity;
import com.branches.material.dto.request.CreateMaterialRequest;
import com.branches.material.dto.response.MaterialResponse;
import com.branches.material.repository.MaterialRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateMaterialService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final MaterialRepository materialRepository;
    private final CheckIfUserHasAccessToMaterialService checkIfUserHasAccessToMaterialService;

    public MaterialResponse execute(String tenantExternalId, CreateMaterialRequest request, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaterialService.execute(currentUserTenant);

        MaterialEntity materialEntity = MaterialEntity.builder()
                .descricao(request.descricao())
                .tenantId(tenantId)
                .build();

        MaterialEntity saved = materialRepository.save(materialEntity);

        return MaterialResponse.from(saved);
    }
}
