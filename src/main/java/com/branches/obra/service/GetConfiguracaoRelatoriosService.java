package com.branches.obra.service;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.response.ConfiguracaoRelatoriosResponse;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetConfiguracaoRelatoriosService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;

    public ConfiguracaoRelatoriosResponse execute(String tenantExternalId, String obraExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());

        return ConfiguracaoRelatoriosResponse.from(obra.getConfiguracaoRelatorios());
    }
}
