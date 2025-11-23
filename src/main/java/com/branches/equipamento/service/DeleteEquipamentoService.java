package com.branches.equipamento.service;

import com.branches.exception.ForbiddenException;
import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.repository.EquipamentoRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeleteEquipamentoService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetEquipamentoByIdAndTenantIdService getEquipamentoByIdAndTenantIdService;
    private final EquipamentoRepository equipamentoRepository;

    public void execute(Long id, String externalTenantId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(externalTenantId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToEquipamento(currentUserTenant);

        EquipamentoEntity equipamentoEntity = getEquipamentoByIdAndTenantIdService.execute(id, tenantId);
        equipamentoEntity.setAtivo(false);

        equipamentoRepository.save(equipamentoEntity);
    }

    private void checkIfUserHasAccessToEquipamento(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getAuthorities().getCadastros().getEquipamentos()) {
            throw new ForbiddenException();
        }
    }
}