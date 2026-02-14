package com.branches.material.service;

import com.branches.material.dto.response.GetItemTopMateriaisResponse;
import com.branches.material.repository.MaterialRepository;
import com.branches.material.repository.projections.ItemTopMateriaisProjection;
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
public class GetTopMateriaisService {

    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;
    private final CheckIfUserCanViewMateriaisDeRelatorioService checkIfUserCanViewMateriaisDeRelatorioService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteMaterialService checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService;
    private final MaterialRepository materialRepository;

    public PageResponse<GetItemTopMateriaisResponse> execute(String tenantExternalId, String obraExternalId, List<UserTenantEntity> userTenants, @Valid PageableRequest pageableRequest) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        if (obraExternalId != null) {
            ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);
            checkIfUserHasAccessToObraService.execute(currentUserTenant, obra.getId());
            checkIfConfiguracaoDeRelatorioDaObraPermiteMaterialService.execute(obra);
        }

        checkIfUserCanViewMateriaisDeRelatorioService.execute(currentUserTenant);

        PageRequest pageRequest = pageableRequest.toPageRequest("quantidadeUso");

        Page<ItemTopMateriaisProjection> materiais = materialRepository.findTopMateriais(tenantId, obraExternalId, pageRequest);

        Page<GetItemTopMateriaisResponse> response = materiais.map(GetItemTopMateriaisResponse::from);

        return PageResponse.from(response);
    }
}