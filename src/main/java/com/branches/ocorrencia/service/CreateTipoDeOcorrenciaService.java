package com.branches.ocorrencia.service;

import com.branches.exception.ForbiddenException;
import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.ocorrencia.dto.request.CreateTipoDeOcorrenciaRequest;
import com.branches.ocorrencia.dto.response.CreateTipoDeOcorrenciaResponse;
import com.branches.ocorrencia.repository.TipoDeOcorrenciaRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateTipoDeOcorrenciaService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    public CreateTipoDeOcorrenciaResponse execute(String tenantExternalId, CreateTipoDeOcorrenciaRequest request, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToTipoDeOcorrencias(currentUserTenant);

        TipoDeOcorrenciaEntity tipoDeOcorrenciaEntity = TipoDeOcorrenciaEntity.builder()
                .descricao(request.descricao())
                .tenantId(tenantId)
                .ativo(true)
                .build();

        TipoDeOcorrenciaEntity saved = tipoDeOcorrenciaRepository.save(tipoDeOcorrenciaEntity);

        return CreateTipoDeOcorrenciaResponse.from(saved);
    }

    private void checkIfUserHasAccessToTipoDeOcorrencias(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getAuthorities().getCadastros().getTiposDeOcorrencia()) {
            throw new ForbiddenException();
        }
    }
}
