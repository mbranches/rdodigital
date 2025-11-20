package com.branches.configuradores.service;

import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.dto.request.CreateModeloDeRelatorioRequest;
import com.branches.configuradores.dto.response.CreateModeloDeRelatorioResponse;
import com.branches.configuradores.repositorio.ModeloDeRelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateModeloDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToModeloDeRelatorioService checkIfUserHasAccessToModeloDeRelatorioService;
    private final CheckIfAlreadyExistsAnotherModeloWithTheTituloService checkIfAlreadyExistsAnotherModeloWithTheTituloService;
    private final ModeloDeRelatorioRepository modeloDeRelatorioRepository;

    public CreateModeloDeRelatorioResponse execute(CreateModeloDeRelatorioRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToModeloDeRelatorioService.execute(userTenant);

        checkIfAlreadyExistsAnotherModeloWithTheTituloService.execute(request.titulo(), tenantId);

        ModeloDeRelatorioEntity toSave = ModeloDeRelatorioEntity.builder()
                .titulo(request.titulo())
                .recorrenciaRelatorio(request.recorrenciaRelatorio())
                .tenantId(tenantId)
                .isDefault(false)
                .build();

        ModeloDeRelatorioEntity saved = modeloDeRelatorioRepository.save(toSave);

        return CreateModeloDeRelatorioResponse.from(saved);
    }
}
