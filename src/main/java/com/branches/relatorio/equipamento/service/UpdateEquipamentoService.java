package com.branches.relatorio.equipamento.service;

import com.branches.exception.ForbiddenException;
import com.branches.relatorio.equipamento.domain.EquipamentoEntity;
import com.branches.relatorio.equipamento.dto.request.UpdateEquipamentoRequest;
import com.branches.relatorio.equipamento.repository.EquipamentoRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateEquipamentoService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetEquipamentoByIdAndTenantIdService getEquipamentoByIdAndTenantIdService;
    private final EquipamentoRepository equipamentoRepository;

    public void execute(Long id, UpdateEquipamentoRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToEquipamento(currentUserTenant);

        EquipamentoEntity equipamentoEntity = getEquipamentoByIdAndTenantIdService.execute(id, tenantId);
        equipamentoEntity.setDescricao(request.descricao());

        equipamentoRepository.save(equipamentoEntity);
    }

    private void checkIfUserHasAccessToEquipamento(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getAuthorities().getCadastros().getEquipamentos()) {
            throw new ForbiddenException();
        }
    }
}
