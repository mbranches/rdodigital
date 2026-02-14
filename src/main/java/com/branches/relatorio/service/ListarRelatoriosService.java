package com.branches.relatorio.service;

import com.branches.relatorio.controller.params.ListRelatoriosRequestParams;
import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.dto.response.PageRelatorioResponse;
import com.branches.relatorio.dto.response.RelatorioResponse;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioCountersProjection;
import com.branches.relatorio.repository.projections.RelatorioProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.shared.pagination.PageableRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListarRelatoriosService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final RelatorioRepository relatorioRepository;

    public PageRelatorioResponse execute(String tenantExternalId, List<UserTenantEntity> userTenants, PageableRequest pageableRequest, ListRelatoriosRequestParams params) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        Boolean canViewOnlyAprovados = currentUserTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados();

        PageRequest pageRequest = PageRequest.of(
                pageableRequest.pageNumber(),
                pageableRequest.pageSize(),
                pageableRequest.sortDirection(),
                "dataInicio",
                "enversCreatedDate"
        );

        boolean userCanViewOnlyAprovadoAndWasPastAnotherStatus = canViewOnlyAprovados && params.status() != null && params.status().equals(StatusRelatorio.APROVADO);
        Page<RelatorioProjection> relatorios = userCanViewOnlyAprovadoAndWasPastAnotherStatus ? Page.empty() : relatorioRepository.findAllByTenantIdAndUserAccessToTheObraPaiWithFilters(
                tenantId,
                currentUserTenant.getObrasPermitidasIds(),
                currentUserTenant.isAdministrador(),
                currentUserTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados(),
                params.status(),
                params.obraId(),
                params.numero(),
                params.dataInicio(),
                pageRequest
        );

        Page<RelatorioResponse> response = relatorios.map(RelatorioResponse::from);

        RelatorioCountersProjection counters = relatorioRepository.findCountByStatus(
                tenantId,
                currentUserTenant.getObrasPermitidasIds(),
                currentUserTenant.isAdministrador(),
                canViewOnlyAprovados,
                params.obraId()
        );

        return PageRelatorioResponse.from(response, counters);
    }
}
