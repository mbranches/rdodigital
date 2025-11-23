package com.branches.ocorrencia.service;

import com.branches.exception.ForbiddenException;
import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.ocorrencia.repository.TipoDeOcorrenciaRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeleteTipoDeOcorrenciaService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetTipoDeOcorrenciaByIdAndTenantIdService getTipoDeOcorrenciaByIdAndTenantIdService;
    private final TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    public void execute(Long id, String externalTenantId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(externalTenantId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToTipoDeOcorrencia(currentUserTenant);

        TipoDeOcorrenciaEntity tipoDeOcorrenciaEntity = getTipoDeOcorrenciaByIdAndTenantIdService.execute(id, tenantId);
        tipoDeOcorrenciaEntity.setAtivo(false);

        tipoDeOcorrenciaRepository.save(tipoDeOcorrenciaEntity);
    }

    private void checkIfUserHasAccessToTipoDeOcorrencia(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getAuthorities().getCadastros().getTiposDeOcorrencia()) {
            throw new ForbiddenException();
        }
    }
}