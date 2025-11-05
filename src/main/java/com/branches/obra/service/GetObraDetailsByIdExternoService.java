package com.branches.obra.service;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.response.GetObraDetailsByIdExternoResponse;
import com.branches.obra.port.LoadObraPort;
import com.branches.shared.dto.TenantDto;
import com.branches.shared.dto.UserDto.UserTenantDto;
import com.branches.shared.exception.ForbiddenException;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.user.domain.enums.PerfilUserTenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetObraDetailsByIdExternoService {
    private final LoadObraPort loadObra;
    private final GetTenantByIdExternoService getTenantByIdExternoService;

    public GetObraDetailsByIdExternoResponse execute(String idExterno, String tenantExternalId, List<UserTenantDto> userTenants) {
        TenantDto tenant = getTenantByIdExternoService.execute(tenantExternalId);
        Long tenantDaObraId = tenant.id();

        UserTenantDto currentUserTenant = getCurrentUserTenant(userTenants, tenantDaObraId);

        ObraEntity obra = loadObra.getObraByIdExternoAndTenantId(idExterno, tenantDaObraId);

        checkIfUserHasAccessToObra(currentUserTenant, obra);

        return GetObraDetailsByIdExternoResponse.from(obra);
    }

    private UserTenantDto getCurrentUserTenant(List<UserTenantDto> userTenants, Long tenantDaObraId) {
        return userTenants.stream()
                .filter(ut -> ut.tenantId().equals(tenantDaObraId))
                .findFirst()
                .orElseThrow(ForbiddenException::new);
    }

    private void checkIfUserHasAccessToObra(UserTenantDto userTenant, ObraEntity obra) {
        boolean userHasAccessToObra = userTenant.perfil().equals(PerfilUserTenant.ADMINISTRADOR)
                || userTenant.obrasPermitidasIds().contains(obra.getId());

        if (!userHasAccessToObra) throw new ForbiddenException();
    }
}
