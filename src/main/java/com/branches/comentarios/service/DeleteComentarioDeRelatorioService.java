package com.branches.comentarios.service;

import com.branches.comentarios.model.ComentarioDeRelatorioEntity;
import com.branches.comentarios.repository.ComentarioDeRelatorioRepository;
import com.branches.exception.ForbiddenException;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeleteComentarioDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteComentarioService checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService;
    private final CheckIfUserCanViewComentariosService checkIfUserCanViewComentariosService;
    private final GetComentarioDeRelatorioByIdAndRelatorioIdService getComentarioDeRelatorioByIdAndRelatorioIdService;
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;

    public void execute(Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteComentarioService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewComentariosService.execute(userTenant);

        ComentarioDeRelatorioEntity comentario = getComentarioDeRelatorioByIdAndRelatorioIdService.execute(id, relatorio.getId());

        checkIfUserCanDeleteComentario(userTenant, comentario);

        comentarioDeRelatorioRepository.delete(comentario);
    }

    private void checkIfUserCanDeleteComentario(UserTenantEntity userTenant, ComentarioDeRelatorioEntity comentario) {
        UserEntity user = userTenant.getUser();
        PerfilUserTenant perfil = userTenant.getPerfil();

        if (comentario.getAutor().equals(user) || perfil.equals(PerfilUserTenant.ADMINISTRADOR)) return;

        throw new ForbiddenException();
    }
}
