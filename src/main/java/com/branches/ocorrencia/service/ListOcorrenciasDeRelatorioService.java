package com.branches.ocorrencia.service;

import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.ocorrencia.dto.response.OcorrenciaDeRelatorioResponse;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListOcorrenciasDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService;
    private final CheckIfUserCanViewOcorrenciasService checkIfUserCanViewOcorrenciasService;
    private final OcorrenciaDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public List<OcorrenciaDeRelatorioResponse> execute(String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewOcorrenciasService.execute(userTenant);

        return equipamentoDeRelatorioRepository.findAllByRelatorioId(relatorio.getId()).stream()
                .map(OcorrenciaDeRelatorioResponse::from)
                .toList();
    }
}
