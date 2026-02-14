package com.branches.ocorrencia.service;

import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.service.GetAtividadeDeRelatorioByIdAndRelatorioIdService;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioCampoPersonalizadoEntity;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateMinutosTotais;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.ocorrencia.dto.request.UpdateOcorrenciaDeRelatorioRequest;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class UpdateOcorrenciaDeRelatorioService {
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final GetOcorrenciaByIdAndRelatorioIdService getOcorrenciaByIdAndRelatorioIdService;
    private final GetTiposDeOcorrenciaByTenantIdAndIdInService getTiposDeOcorrenciaByTenantIdAndIdInService;
    private final CalculateMinutosTotais calculateMinutosTotais;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService;
    private final CheckIfUserCanViewOcorrenciasService checkIfUserCanViewOcorrenciasService;
    private final GetAtividadeDeRelatorioByIdAndRelatorioIdService getAtividadeDeRelatorioByIdAndRelatorioIdService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    public void execute(UpdateOcorrenciaDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewOcorrenciasService.execute(userTenant);

        OcorrenciaDeRelatorioEntity entity = getOcorrenciaByIdAndRelatorioIdService.execute(id, relatorio.getId());

        entity.setDescricao(request.descricao());

        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setMinutosTotais(calculateMinutosTotais.execute(request.horaInicio(), request.horaFim(), null));

        AtividadeDeRelatorioEntity atividadeVinculada = request.atividadeVinculadaId() != null
                ? getAtividadeDeRelatorioByIdAndRelatorioIdService.execute(request.atividadeVinculadaId(), relatorio.getId())
                : null;
        entity.setAtividadeVinculada(atividadeVinculada);

        List<TipoDeOcorrenciaEntity> tiposDeOcorrencia = getTiposDeOcorrenciaList(request.tiposOcorrenciaIds(), tenantId);
        entity.getTiposDeOcorrencia().clear();
        entity.getTiposDeOcorrencia().addAll(tiposDeOcorrencia);

        List<OcorrenciaDeRelatorioCampoPersonalizadoEntity> camposPersonalizadosToSave = getCamposPersonalizadosToSave(request.camposPersonalizados(), tenantId, entity);

        entity.getCamposPersonalizados().clear();
        entity.getCamposPersonalizados().addAll(
                camposPersonalizadosToSave
        );

        ocorrenciaDeRelatorioRepository.save(entity);
    }

    private List<OcorrenciaDeRelatorioCampoPersonalizadoEntity> getCamposPersonalizadosToSave(List<CampoPersonalizadoRequest> requestList, Long tenantId, OcorrenciaDeRelatorioEntity ocorrenciaDeRelatorioEntity) {
        if (requestList == null || requestList.isEmpty()) {
            return List.of();
        }

        return requestList.stream()
                .map(request -> request.toEntity(tenantId))
                .map(campoPersonalizadoEntity ->
                        OcorrenciaDeRelatorioCampoPersonalizadoEntity.from(ocorrenciaDeRelatorioEntity, campoPersonalizadoEntity, tenantId)
                ).toList();
    }

    private List<TipoDeOcorrenciaEntity> getTiposDeOcorrenciaList(List<Long> ids, Long tenantId) {
        return ids != null && !ids.isEmpty() ? getTiposDeOcorrenciaByTenantIdAndIdInService.execute(tenantId, new HashSet<>(ids))
                : List.of();
    }
}
