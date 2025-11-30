package com.branches.obra.service;

import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.dto.request.UpdateConfiguracoesDeRelatoriosDeObraRequest;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateConfiguracoesDeRelatoriosDeObraService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserCanEditObraService checkIfUserCanEditObraService;
    private final GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;

    @Transactional
    public void execute(UpdateConfiguracoesDeRelatoriosDeObraRequest request, String obraExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);
        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        ObraEntity obra = getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId);

        checkIfUserCanEditObraService.execute(currentUserTenant, obra.getId());

        ConfiguracaoRelatoriosEntity configuracaoRelatorios = obra.getConfiguracaoRelatorios();
        configuracaoRelatorios.setRecorrenciaRelatorio(request.recorrenciaRelatorio());
        configuracaoRelatorios.setShowCondicaoClimatica(request.showCondicaoClimatica());
        configuracaoRelatorios.setShowMaoDeObra(request.showMaoDeObra());
        configuracaoRelatorios.setShowEquipamentos(request.showEquipamentos());
        configuracaoRelatorios.setShowAtividades(request.showAtividades());
        configuracaoRelatorios.setShowOcorrencias(request.showOcorrencias());
        configuracaoRelatorios.setShowComentarios(request.showComentarios());
        configuracaoRelatorios.setShowMateriais(request.showMateriais());
        configuracaoRelatorios.setShowHorarioDeTrabalho(request.showHorarioDeTrabalho());
        configuracaoRelatorios.setShowFotos(request.showFotos());
        configuracaoRelatorios.setShowVideos(request.showVideos());
    }
}
