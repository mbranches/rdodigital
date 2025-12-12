package com.branches.maodeobra.service;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.dto.response.MaoDeObraResponse;
import com.branches.maodeobra.repository.MaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListAllMaoDeObraService {
    private final MaoDeObraRepository maoDeObraRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;

    public List<MaoDeObraResponse> execute(String tenantExternalId, TipoMaoDeObra tipoMaoDeObra, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        getCurrentUserTenantService.execute(userTenants, tenantId);

        List<MaoDeObraEntity> maoDeObraList = maoDeObraRepository.findAllByTenantIdAndTipoAndAtivoIsTrue(tenantId, tipoMaoDeObra);

        return maoDeObraList.stream()
                .map(MaoDeObraResponse::from)
                .toList();
    }
}
