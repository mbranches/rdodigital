package com.branches.configuradores.service;

import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.dto.response.ModeloDeRelatorioResponse;
import com.branches.configuradores.repositorio.ModeloDeRelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListAllModelosDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToModeloDeRelatorioService checkIfUserHasAccessToModeloDeRelatorioService;
    private final ModeloDeRelatorioRepository modeloDeRelatorioRepository;

    public List<ModeloDeRelatorioResponse> execute(String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);
        checkIfUserHasAccessToModeloDeRelatorioService.execute(currentUserTenant);

        List<ModeloDeRelatorioEntity> response = modeloDeRelatorioRepository.findAllByTenantIdAndAtivoIsTrueOrderByEnversCreatedDateAsc(tenantId);

        return response.stream()
                .map(ModeloDeRelatorioResponse::from)
                .toList();
    }
}
