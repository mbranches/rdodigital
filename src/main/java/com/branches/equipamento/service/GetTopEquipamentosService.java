package com.branches.equipamento.service;

import com.branches.equipamento.dto.response.GetItemTopEquipamentosResponse;
import com.branches.equipamento.repository.EquipamentoRepository;
import com.branches.equipamento.repository.projections.ItemTopEquipamentosProjection;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdExternoAndTenantIdService;
import com.branches.shared.pagination.PageResponse;
import com.branches.shared.pagination.PageableRequest;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTopEquipamentosService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanViewEquipamentosService checkIfUserCanViewEquipamentosService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService;
    private final EquipamentoRepository equipamentoRepository;

    public PageResponse<GetItemTopEquipamentosResponse> execute(String tenantExternalId, String obraExternalId, List<UserTenantEntity> userTenants, @Valid PageableRequest pageableRequest) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        if (obraExternalId != null) {
            ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);
            checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());
            checkIfConfiguracaoDeRelatorioDaObraPermiteEquipamentoService.execute(obra);
        }

        checkIfUserCanViewEquipamentosService.execute(currentUserTenant);

        PageRequest pageRequest = pageableRequest.toPageRequest("quantidadeUso");

        Page<ItemTopEquipamentosProjection> equipamentos = equipamentoRepository.findTopEquipamentos(tenantId, obraExternalId, pageRequest);

        Page<GetItemTopEquipamentosResponse> response = equipamentos.map(GetItemTopEquipamentosResponse::from);

        return PageResponse.from(response);
    }
}