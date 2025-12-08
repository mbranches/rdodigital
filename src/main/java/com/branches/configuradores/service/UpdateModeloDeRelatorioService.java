package com.branches.configuradores.service;

import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.dto.request.UpdateModeloDeRelatorioRequest;
import com.branches.configuradores.repositorio.ModeloDeRelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateModeloDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final CheckIfUserHasAccessToModeloDeRelatorioService checkIfUserHasAccessToModeloDeRelatorioService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfAlreadyExistsAnotherModeloWithTheTituloService checkIfAlreadyExistsAnotherModeloWithTheTituloService;
    private final ModeloDeRelatorioRepository modeloDeRelatorioRepository;
    private final GetModeloDeRelatorioByIdAndTenantIdService getModeloDeRelatorioByIdAndTenantIdService;

    public void execute(UpdateModeloDeRelatorioRequest request, Long id, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToModeloDeRelatorioService.execute(currentUserTenant);

        ModeloDeRelatorioEntity modeloToUpdate = getModeloDeRelatorioByIdAndTenantIdService.execute(id, tenantId);

        checkIfAlreadyExistsAnotherModeloWithTheTituloService.executeExcludingId(request.titulo(), tenantId, id);

        modeloToUpdate.setTitulo(request.titulo());
        modeloToUpdate.setRecorrenciaRelatorio(request.recorrenciaRelatorio());
        modeloToUpdate.setShowCondicaoClimatica(request.showCondicaoClimatica());
        modeloToUpdate.setShowMaoDeObra(request.showMaoDeObra());
        modeloToUpdate.setShowEquipamentos(request.showEquipamentos());
        modeloToUpdate.setShowAtividades(request.showAtividades());
        modeloToUpdate.setShowOcorrencias(request.showOcorrencias());
        modeloToUpdate.setShowComentarios(request.showComentarios());
        modeloToUpdate.setShowMateriais(request.showMateriais());
        modeloToUpdate.setShowHorarioDeTrabalho(request.showHorarioDeTrabalho());
        modeloToUpdate.setShowFotos(request.showFotos());
        modeloToUpdate.setShowVideos(request.showVideos());

        modeloDeRelatorioRepository.save(modeloToUpdate);
    }
}
