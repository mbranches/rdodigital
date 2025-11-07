package com.branches.obra.service;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.response.GetObraDetailsByIdExternoResponse;
import com.branches.exception.ForbiddenException;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetObraDetailsByIdExternoService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;

    public GetObraDetailsByIdExternoResponse execute(String idExterno, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantDaObraId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantDaObraId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(idExterno, tenantDaObraId);

        checkIfUserHasAccessToObra(currentUserTenant, obra);

        return GetObraDetailsByIdExternoResponse.from(obra);
    }

    private void checkIfUserHasAccessToObra(UserTenantEntity userTenant, ObraEntity obra) {
        boolean userHasAccessToObra = userTenant.getPerfil().equals(PerfilUserTenant.ADMINISTRADOR)
                || userTenant.getObrasPermitidasIds().contains(obra.getId());

        if (!userHasAccessToObra) throw new ForbiddenException();
    }
}
