package com.branches.comentarios.service;

import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.exception.ForbiddenException;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
import com.branches.comentarios.dto.request.UpdateComentarioDeRelatorioRequest;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UpdateComentarioDeRelatorioService {
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final GetComentarioDeRelatorioByIdAndRelatorioIdService getComentarioDeRelatorioByIdAndRelatorioIdService;
    private final CheckIfUserCanViewComentariosService checkIfUserCanViewComentariosService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteComentarioService checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService;

    public void execute(UpdateComentarioDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());

        checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService.execute(relatorio.getObraId(), tenantId);

        checkIfUserCanViewComentariosService.execute(userTenant);

        ComentarioDeRelatorioEntity entity = getComentarioDeRelatorioByIdAndRelatorioIdService.execute(id, relatorio.getId());

        checkIfUserCanUpdateComentario(userTenant, entity);

        entity.setDescricao(request.descricao());

        List<CampoPersonalizadoRequest> campoPersonalizadoRequest = request.camposPersonalizados() != null
                ? request.camposPersonalizados()
                : List.of();
        entity.getCamposPersonalizados().clear();
        entity.getCamposPersonalizados().addAll(
                campoPersonalizadoRequest.stream().map(c -> c.toEntity(tenantId)).toList()
        );

        comentarioDeRelatorioRepository.save(entity);
    }

    private void checkIfUserCanUpdateComentario(UserTenantEntity userTenant, ComentarioDeRelatorioEntity entity) {
        UserEntity user = userTenant.getUser();

        if (entity.getAutor().equals(user)) return;

        throw new ForbiddenException();
    }
}
