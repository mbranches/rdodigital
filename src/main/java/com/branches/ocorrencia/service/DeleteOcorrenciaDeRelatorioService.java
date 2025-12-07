package com.branches.ocorrencia.service;

import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GenerateRelatorioFileToUsersService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.ItemRelatorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeleteOcorrenciaDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService;
    private final CheckIfUserCanViewOcorrenciasService checkIfUserCanViewOcorrenciasService;
    private final GetOcorrenciaByIdAndRelatorioIdService getOcorrenciaByIdAndRelatorioIdService;
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final GenerateRelatorioFileToUsersService generateRelatorioFileToUsersService;

    public void execute(Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService.execute(relatorio.getObraId(), tenantId);

        checkIfUserCanViewOcorrenciasService.execute(userTenant);

        OcorrenciaDeRelatorioEntity ocorrencia = getOcorrenciaByIdAndRelatorioIdService.execute(id, relatorio.getId());

        ocorrenciaDeRelatorioRepository.delete(ocorrencia);

        generateRelatorioFileToUsersService.executeOnlyToNecessaryUsers(relatorio.getId(), ItemRelatorio.OCORRENCIAS);
    }
}
