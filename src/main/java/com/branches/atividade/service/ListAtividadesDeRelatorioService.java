package com.branches.atividade.service;

import com.branches.atividade.dto.response.AtividadeDeRelatorioResponse;
import com.branches.atividade.repository.AtividadeDeRelatorioRepository;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
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
public class ListAtividadesDeRelatorioService {
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteAtividade checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade;
    private final CheckIfUserCanViewAtividadesService checkIfUserCanViewAtividadesService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public List<AtividadeDeRelatorioResponse> execute(String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfConfiguracaoDeRelatorioDaObraPermiteAtividade.execute(relatorio, tenantId);
        checkIfUserCanViewAtividadesService.execute(userTenant);

        return atividadeDeRelatorioRepository.findAllByRelatorioId(relatorio.getId()).stream()
                .map(AtividadeDeRelatorioResponse::from)
                .toList();
    }
}
