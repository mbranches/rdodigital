package com.branches.ocorrencia.service;

import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.ocorrencia.dto.response.TipoDeOcorrenciaResponse;
import com.branches.ocorrencia.repository.TipoDeOcorrenciaRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListAllTiposDeOcorrenciaService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    public List<TipoDeOcorrenciaResponse> execute(String externalTenantId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(externalTenantId);

        getCurrentUserTenantService.execute(userTenants, tenantId);

        List<TipoDeOcorrenciaEntity> tipoDeOcorrenciaEntityList = tipoDeOcorrenciaRepository.findAllByTenantIdAndAtivoIsTrue(tenantId);

        return tipoDeOcorrenciaEntityList.stream()
                .map(TipoDeOcorrenciaResponse::from)
                .toList();
    }
}
