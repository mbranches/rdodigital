package com.branches.obra.service;

import com.branches.relatorio.domain.enums.StatusRelatorio;
import com.branches.relatorio.dto.response.RelatorioResponse;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.relatorio.repository.projections.RelatorioProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
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

    public List<RelatorioResponse> execute(String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants, PageRequest pageRequest) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        Long obraId = getObraIdByIdExternoAndTenantService.execute(obraExternalId, tenantId);

        Boolean canViewOnlyAprovados = currentUserTenant.getAuthorities().getRelatorios().getCanViewOnlyAprovados();

        List<RelatorioProjection> relatorios = canViewOnlyAprovados ? relatorioRepository.findAllByObraIdAndStatusProjection(obraId, StatusRelatorio.APROVADO, pageRequest) :
                relatorioRepository.findAllByObraIdProjection(obraId, pageRequest);

        return relatorios.stream()
                .map(RelatorioResponse::from)
                .toList();
    }
}
