package com.branches.relatorio.maodeobra.service;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.dto.response.MaoDeObraResponse;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListAllMaoDeObraService {
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final MaoDeObraRepository maoDeObraRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    public List<MaoDeObraResponse> execute(String tenantExternalId, TipoMaoDeObra tipoMaoDeObra, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToMaoDeObraService.execute(currentUserTenant);

        List<MaoDeObraEntity> maoDeObraList = maoDeObraRepository.findAllByTenantIdAndTipoAndAtivoIsTrue(tenantId, tipoMaoDeObra);

        return maoDeObraList.stream()
                .map(MaoDeObraResponse::from)
                .toList();
    }
}
