package com.branches.material.service;

import com.branches.material.domain.MaterialEntity;
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
public class ListAllMateriaisService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final MaterialRepository materialRepository;

    public List<MaterialResponse> execute(String externalTenantId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(externalTenantId);

        getCurrentUserTenantService.execute(userTenants, tenantId);

        List<MaterialEntity> materialEntityList = materialRepository.findAllByTenantIdAndAtivoIsTrue(tenantId);

        return materialEntityList.stream()
                .map(MaterialResponse::from)
                .toList();
    }
}
