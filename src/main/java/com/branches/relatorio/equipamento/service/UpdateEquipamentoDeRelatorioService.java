package com.branches.relatorio.equipamento.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdAndTenantIdService;
import com.branches.relatorio.equipamento.domain.EquipamentoEntity;
import com.branches.relatorio.rdo.domain.EquipamentoDeRelatorioEntity;
import com.branches.relatorio.rdo.domain.RelatorioEntity;
import com.branches.relatorio.rdo.dto.request.UpdateEquipamentoDeRelatorioRequest;
import com.branches.relatorio.rdo.repository.EquipamentoDeRelatorioRepository;
import com.branches.relatorio.rdo.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.rdo.service.GetRelatorioByIdExternoAndTenantIdService;
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
    private final GetObraByIdAndTenantIdService getObraByIdAndTenantIdService;
    private final GetEquipamentoByIdAndTenantIdService getEquipamentoByIdAndTenantIdService;

    public void execute(UpdateEquipamentoDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamento(relatorio, tenantId);

        checkIfUserCanViewEquipamentos(userTenant);

        EquipamentoDeRelatorioEntity entity = getEquipamentoDeRelatorioByIdAndRelatorioIdService.execute(id, relatorio.getId());

        EquipamentoEntity equipamento = getEquipamentoByIdAndTenantIdService.execute(request.equipamentoId(), tenantId);

        entity.setEquipamento(equipamento);
        entity.setQuantidade(request.quantidade());

        equipamentoDeRelatorioRepository.save(entity);
    }

    private void checkIfUserCanViewEquipamentos(UserTenantEntity userTenant) {
        if (userTenant.getAuthorities().getItensDeRelatorio().getEquipamentos()) return;

        throw new ForbiddenException();
    }

    private void checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamento(RelatorioEntity relatorio, Long tenantId) {
        ObraEntity obra = getObraByIdAndTenantIdService.execute(relatorio.getObraId(), tenantId);

        if (obra.getConfiguracaoRelatorios().getShowEquipamentos()) return;

        throw new ForbiddenException();
    }
}
