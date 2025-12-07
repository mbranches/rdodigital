package com.branches.condicaoclimatica.service;

import com.branches.condicaoclimatica.domain.CondicaoClimaticaEntity;
import com.branches.condicaoclimatica.dto.request.CondicaoClimaticaRequest;
import com.branches.condicaoclimatica.dto.request.UpdateCondicaoClimaticaDeRelatorioRequest;
import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdAndTenantIdService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.repository.RelatorioRepository;
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
public class UpdateCondicaoClimaticaDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final GetObraByIdAndTenantIdService getObraByIdAndTenantIdService;
    private final RelatorioRepository relatorioRepository;
    private final GenerateRelatorioFileToUsersService generateRelatorioFileToUsersService;

    public void execute(UpdateCondicaoClimaticaDeRelatorioRequest request, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteCondicaoClimatica(relatorio, tenantId);

        checkIfUserCanViewCondicaoClimatica(userTenant);

        updateCondicaoClimatica(request.condicaoClimaticaManha(), relatorio.getCaracteristicasManha());
        updateCondicaoClimatica(request.condicaoClimaticaTarde(), relatorio.getCaracteristicasTarde());
        updateCondicaoClimatica(request.condicaoClimaticaNoite(), relatorio.getCaracteristicasNoite());
        relatorio.setIndiciePluviometrico(request.indicePluviometrico());

        relatorioRepository.save(relatorio);

        generateRelatorioFileToUsersService.executeOnlyToNecessaryUsers(relatorio.getId(), ItemRelatorio.CONDICAO_CLIMATICA);
    }

    private void updateCondicaoClimatica(CondicaoClimaticaRequest request, CondicaoClimaticaEntity toUpdate) {
        toUpdate.setClima(request.clima());
        toUpdate.setCondicaoDoTempo(request.condicaoDoTempo());
        toUpdate.setAtivo(request.ativo());
    }

    private void checkIfUserCanViewCondicaoClimatica(UserTenantEntity userTenant) {
        if (!userTenant.getAuthorities().getItensDeRelatorio().getCondicaoDoClima()) {
            throw new ForbiddenException();
        }
    }

    private void checkIfConfiguracaoDeRelatorioDaObraPermiteCondicaoClimatica(RelatorioEntity relatorio, Long tenantId) {
        ObraEntity obra = getObraByIdAndTenantIdService.execute(relatorio.getObraId(), tenantId);

        if (obra.getConfiguracaoRelatorios().getShowCondicaoClimatica()) return;

        throw new ForbiddenException();
    }
}
