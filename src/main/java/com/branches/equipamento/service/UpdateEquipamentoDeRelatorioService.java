package com.branches.equipamento.service;

import com.branches.equipamento.domain.EquipamentoDeRelatorioEntity;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.equipamento.dto.request.UpdateEquipamentoDeRelatorioRequest;
import com.branches.equipamento.repository.EquipamentoDeRelatorioRepository;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class UpdateEquipamentoDeRelatorioService {

    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final GetEquipamentoDeRelatorioByIdAndRelatorioIdService getEquipamentoDeRelatorioByIdAndRelatorioIdService;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService;
    private final CheckIfUserCanViewEquipamentosService checkIfUserCanViewEquipamentosService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public void execute(UpdateEquipamentoDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewEquipamentosService.execute(userTenant);

        EquipamentoDeRelatorioEntity entity = getEquipamentoDeRelatorioByIdAndRelatorioIdService.execute(id, relatorio.getId());

        entity.setQuantidade(request.quantidade());

        equipamentoDeRelatorioRepository.save(entity);
    }
}
