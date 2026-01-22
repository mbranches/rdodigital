package com.branches.comentarios.service;

import com.branches.comentarios.dto.request.CreateComentarioDeRelatorioRequest;
import com.branches.comentarios.dto.response.CreateComentarioDeRelatorioResponse;
import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.obra.controller.CheckIfUserHasAccessToObraService;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateComentarioDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteComentarioService checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService;
    private final CheckIfUserCanViewComentariosService checkIfUserCanViewComentariosService;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final CheckIfUserCanAddComentariosToRelatorioService checkIfUserCanAddComentariosToRelatorioService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public CreateComentarioDeRelatorioResponse execute(CreateComentarioDeRelatorioRequest request, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewComentariosService.execute(userTenant);
        checkIfUserCanAddComentariosToRelatorioService.execute(userTenant);

        ComentarioDeRelatorioEntity toSave = ComentarioDeRelatorioEntity.builder()
                .relatorio(relatorio)
                .descricao(request.descricao())
                .autor(userTenant.getUser())
                .tenantId(tenantId)
                .build();

        ComentarioDeRelatorioEntity saved = comentarioDeRelatorioRepository.save(toSave);

        return CreateComentarioDeRelatorioResponse.from(saved);
    }
}
