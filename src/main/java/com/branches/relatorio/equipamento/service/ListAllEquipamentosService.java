package com.branches.relatorio.equipamento.service;

import com.branches.exception.ForbiddenException;
import com.branches.relatorio.equipamento.domain.EquipamentoEntity;
import com.branches.relatorio.equipamento.dto.response.EquipamentoResponse;
import com.branches.relatorio.equipamento.repository.EquipamentoRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListAllEquipamentosService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final EquipamentoRepository equipamentoRepository;

    public List<EquipamentoResponse> execute(String externalTenantId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(externalTenantId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToEquipamentos(currentUserTenant);

        List<EquipamentoEntity> equipamentoEntityList = equipamentoRepository.findAllByTenantIdAndAtivoIsTrue(tenantId);

        return equipamentoEntityList.stream()
                .map(EquipamentoResponse::from)
                .toList();
    }

    private void checkIfUserHasAccessToEquipamentos(UserTenantEntity currentUserTenant) {
        if (!currentUserTenant.getAuthorities().getCadastros().getEquipamentos()) {
            throw new ForbiddenException();
        }
    }
}
