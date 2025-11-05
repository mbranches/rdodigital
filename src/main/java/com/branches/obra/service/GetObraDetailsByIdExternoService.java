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

    public GetObraDetailsByIdExternoResponse execute(String idExterno, String tenantExternalId, List<UserTenantDto> userTenants, List<Long> userAllowedObraIds) {
        TenantDto tenant = getTenantByIdExternoService.execute(tenantExternalId);
        Long tenantDaObraId = tenant.id();

        List<Long> userTenantsIds = userTenants.stream().map(UserTenantDto::tenantId).toList();

        if (userTenantsIds.contains(tenantDaObraId)) throw new ForbiddenException();

        ObraEntity obra = loadObra.getObraByIdExternoAndTenantId(idExterno, tenantDaObraId);

        checkIfUserHasAccessToObra(userTenants, userAllowedObraIds, obra);

        return GetObraDetailsByIdExternoResponse.from(obra);
    }

    private void checkIfUserHasAccessToObra(List<UserTenantDto> userTenants, List<Long> userAllowedObraIds, ObraEntity obra) {
        boolean userHasAccessToObra = userTenants.stream()
                .anyMatch(ut -> ut.perfil().equals(PerfilUserTenant.ADMINISTRADOR) || userAllowedObraIds.contains(obra.getId()));

        if (!userHasAccessToObra) throw new ForbiddenException();
    }
}
