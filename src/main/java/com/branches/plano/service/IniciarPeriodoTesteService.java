package com.branches.plano.service;

import com.branches.plano.domain.PeriodoTesteEntity;
import com.branches.plano.repository.PeriodoTesteRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class IniciarPeriodoTesteService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfTenantAlreadyCanIniciarPeriodoTesteService checkIfTenantAlreadyCanIniciarPeriodoTesteService;
    private static final long PERIODO_TESTE_DIAS = 10;
    private final PeriodoTesteRepository periodoTesteRepository;

    public void execute(String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfTenantAlreadyCanIniciarPeriodoTesteService.execute(tenantId);

        PeriodoTesteEntity entity = PeriodoTesteEntity.builder()
                .tenantId(tenantId)
                .build();
        entity.iniciar(PERIODO_TESTE_DIAS);

        periodoTesteRepository.save(entity);
    }
}
