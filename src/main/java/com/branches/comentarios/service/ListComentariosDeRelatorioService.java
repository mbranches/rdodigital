package com.branches.comentarios.service;

import com.branches.comentarios.dto.response.ComentarioDeRelatorioResponse;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListComentariosDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteComentarioService checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService;
    private final CheckIfUserCanViewComentariosService checkIfUserCanViewComentariosService;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public List<ComentarioDeRelatorioResponse> execute(String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewComentariosService.execute(userTenant);

        return comentarioDeRelatorioRepository.findAllByRelatorioId(relatorio.getId()).stream()
                .map(ComentarioDeRelatorioResponse::from)
                .toList();
    }
}
