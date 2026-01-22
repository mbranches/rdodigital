package com.branches.maodeobra.service;

import com.branches.exception.BadRequestException;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.domain.enums.PresencaMaoDeObra;
import com.branches.maodeobra.dto.request.CreateMaoDeObraDeRelatorioRequest;
import com.branches.maodeobra.dto.response.CreateMaoDeObraDeRelatorioResponse;
import com.branches.maodeobra.repository.MaoDeObraDeRelatorioRepository;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateHorasTotais;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateMaoDeObraDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService;
    private final CheckIfUserCanViewMaoDeObraService checkIfUserCanViewMaoDeObraService;
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final GetMaoDeObraByIdAndTenantIdService getMaoDeObraByIdAndTenantIdService;
    private final CalculateHorasTotais calculateHorasTotais;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public CreateMaoDeObraDeRelatorioResponse execute(CreateMaoDeObraDeRelatorioRequest request, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        boolean maoDeObraAlreadyAdded = maoDeObraDeRelatorioRepository.existsByRelatorioIdAndMaoDeObraId(relatorio.getId(), request.maoDeObraId());

        if (maoDeObraAlreadyAdded) {
            throw new BadRequestException("Mão de obra já adicionada ao relatório");
        }

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteMaoDeObraService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewMaoDeObraService.execute(userTenant);

        MaoDeObraEntity maoDeObra = getMaoDeObraByIdAndTenantIdService.execute(request.maoDeObraId(), tenantId);

        MaoDeObraDeRelatorioEntity toSave = new MaoDeObraDeRelatorioEntity();
        toSave.setRelatorio(relatorio);
        toSave.setMaoDeObra(maoDeObra);
        toSave.setFuncao(maoDeObra.getFuncao());
        toSave.setPresenca(request.presenca());
        toSave.setTenantId(tenantId);

        if (request.presenca().equals(PresencaMaoDeObra.PRESENTE)) {
            toSave.setHoraInicio(request.horaInicio());
            toSave.setHoraFim(request.horaFim());
            toSave.setMinutosIntervalo(request.minutosIntervalo());
            toSave.setHorasTrabalhadas(calculateHorasTotais.execute(request.horaInicio(), request.horaFim(), request.minutosIntervalo()));
        }

        MaoDeObraDeRelatorioEntity saved = maoDeObraDeRelatorioRepository.save(toSave);

        return CreateMaoDeObraDeRelatorioResponse.from(saved);
    }
}
