package com.branches.maodeobra.service;

import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.maodeobra.domain.enums.PresencaMaoDeObra;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateMinutosTotais;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.maodeobra.dto.request.UpdateMaoDeObraDeRelatorioRequest;
import com.branches.maodeobra.repository.MaoDeObraDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class UpdateMaoDeObraDeRelatorioService {
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final GetMaoDeObraDeRelatorioByIdAndRelatorioId getMaoDeObraDeRelatorioByIdAndRelatorioId;
    private final CalculateMinutosTotais calculateMinutosTotais;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService;
    private final CheckIfUserCanViewMaoDeObraService checkIfUserCanViewMaoDeObraService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public void execute(UpdateMaoDeObraDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewMaoDeObraService.execute(userTenant);

        MaoDeObraDeRelatorioEntity entity = getMaoDeObraDeRelatorioByIdAndRelatorioId.execute(id, relatorio.getId());

        entity.setPresenca(request.presenca());

        if (request.presenca().equals(PresencaMaoDeObra.PRESENTE)) {
            entity.setHoraInicio(request.horaInicio());
            entity.setHoraFim(request.horaFim());
            entity.setMinutosIntervalo(request.minutosIntervalo());
            int minutosTrabalhos = calculateMinutosTotais.execute(request.horaInicio(), request.horaFim(), request.minutosIntervalo());
            entity.setMinutosTrabalhados(minutosTrabalhos);
        }

        maoDeObraDeRelatorioRepository.save(entity);
    }
}
