package com.branches.equipamento.service;

import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.dto.request.CreateEquipamentoDeRelatorioRequest;
import com.branches.equipamento.dto.response.CreateEquipamentoDeRelatorioResponse;
import com.branches.equipamento.repository.EquipamentoDeRelatorioRepository;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateEquipamentoDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService;
    private final CheckIfUserCanViewEquipamentosService checkIfUserCanViewEquipamentosService;
    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final GetEquipamentoByIdAndTenantIdService getEquipamentoByIdAndTenantIdService;

    public CreateEquipamentoDeRelatorioResponse execute(CreateEquipamentoDeRelatorioRequest request, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService.execute(relatorio.getObraId(), tenantId);

        checkIfUserCanViewEquipamentosService.execute(userTenant);

        EquipamentoEntity equipamento = getEquipamentoByIdAndTenantIdService.execute(request.equipamentoId(), tenantId);

        EquipamentoDeRelatorioEntity toSave = EquipamentoDeRelatorioEntity.builder()
                .relatorio(relatorio)
                .equipamento(equipamento)
                .quantidade(request.quantidade())
                .build();

        EquipamentoDeRelatorioEntity saved = equipamentoDeRelatorioRepository.save(toSave);

        return CreateEquipamentoDeRelatorioResponse.from(saved);
    }
}
