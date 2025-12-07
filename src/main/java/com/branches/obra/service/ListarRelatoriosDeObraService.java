package com.branches.obra.service;

import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.dto.response.RelatorioResponse;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.PageResponse;
import com.branches.utils.PageableRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListarRelatoriosDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final RelatorioRepository relatorioRepository;
    private final GetObraIdByIdExternoService getObraIdByIdExternoAndTenantService;

    public PageResponse<RelatorioResponse> execute(String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants, PageableRequest pageableRequest) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        Long obraId = getObraIdByIdExternoAndTenantService.execute(obraExternalId, tenantId);

        Boolean canViewOnlyAprovados = currentUserTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados();

        PageRequest pageRequest = PageRequest.of(pageableRequest.pageNumber(), pageableRequest.pageSize(), pageableRequest.sortDirection(), "dataInicio");

        Page<RelatorioProjection> relatorios = canViewOnlyAprovados ? relatorioRepository.findAllByObraIdAndStatusProjection(obraId, StatusRelatorio.APROVADO, pageRequest) :
                relatorioRepository.findAllByObraIdProjection(obraId, pageRequest);

        Page<RelatorioResponse> response = relatorios.map(RelatorioResponse::from);

        return PageResponse.from(response);
    }
}
