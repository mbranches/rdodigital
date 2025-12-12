package com.branches.equipamento.service;

import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.dto.response.EquipamentoResponse;
import com.branches.equipamento.repository.EquipamentoRepository;
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

        getCurrentUserTenantService.execute(userTenants, tenantId);

        List<EquipamentoEntity> equipamentoEntityList = equipamentoRepository.findAllByTenantIdAndAtivoIsTrue(tenantId);

        return equipamentoEntityList.stream()
                .map(EquipamentoResponse::from)
                .toList();
    }
}
