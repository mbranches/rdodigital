package com.branches.ocorrencia.service;

import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.atividade.service.GetAtividadeDeRelatorioByIdAndRelatorioIdService;
import com.branches.obra.service.CheckIfUserHasAccessToObraService;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioCampoPersonalizadoEntity;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.ocorrencia.dto.request.CreateOcorrenciaDeRelatorioRequest;
import com.branches.ocorrencia.dto.response.CreateOcorrenciaDeRelatorioResponse;
import com.branches.ocorrencia.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateMinutosTotais;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateOcorrenciaDeRelatorioService {
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final CheckIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService;
    private final CheckIfUserCanViewOcorrenciasService checkIfUserCanViewOcorrenciasService;
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final GetTiposDeOcorrenciaByTenantIdAndIdInService getTiposDeOcorrenciaByTenantIdAndIdInService;
    private final CalculateMinutosTotais calculateMinutosTotais;
    private final GetAtividadeDeRelatorioByIdAndRelatorioIdService getAtividadeDeRelatorioByIdAndRelatorioIdService;
    private final CheckIfUserHasAccessToObraService checkIfUserHasAccessToObraService;

    @Transactional
    public CreateOcorrenciaDeRelatorioResponse execute(CreateOcorrenciaDeRelatorioRequest request, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfUserHasAccessToObraService.execute(userTenant, relatorio.getObraId());
        checkIfUserHasAccessToEditRelatorioService.execute(userTenant, relatorio.getStatus());
        checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrenciaService.execute(relatorio.getObraId(), tenantId);
        checkIfUserCanViewOcorrenciasService.execute(userTenant);

        List<TipoDeOcorrenciaEntity> tiposDeOcorrencia = request.tiposOcorrenciaIds() != null && !request.tiposOcorrenciaIds().isEmpty() ?
                getTiposDeOcorrenciaByTenantIdAndIdInService.execute(tenantId, new HashSet<>(request.tiposOcorrenciaIds()))
                : null;

        OcorrenciaDeRelatorioEntity toSave = OcorrenciaDeRelatorioEntity.builder()
                .relatorio(relatorio)
                .descricao(request.descricao())
                .horaInicio(request.horaInicio())
                .horaFim(request.horaFim())
                .minutosTotais(calculateMinutosTotais.execute(request.horaInicio(), request.horaFim(), null))
                .tiposDeOcorrencia(tiposDeOcorrencia)
                .tenantId(tenantId)
                .build();

        List<OcorrenciaDeRelatorioCampoPersonalizadoEntity> camposPersonalizados = getCamposPersonalizadosToSave(request.camposPersonalizados(), toSave, tenantId);
        toSave.setCamposPersonalizados(camposPersonalizados);

        if (request.atividadeVinculadaId() != null) {
            AtividadeDeRelatorioEntity atividade = getAtividadeDeRelatorioByIdAndRelatorioIdService.execute(request.atividadeVinculadaId(), relatorio.getId());

            toSave.setAtividadeVinculada(atividade);
        }

        OcorrenciaDeRelatorioEntity saved = ocorrenciaDeRelatorioRepository.save(toSave);

        return CreateOcorrenciaDeRelatorioResponse.from(saved);
    }

    private List<OcorrenciaDeRelatorioCampoPersonalizadoEntity> getCamposPersonalizadosToSave(List<CampoPersonalizadoRequest> requestList, OcorrenciaDeRelatorioEntity toSave, Long tenantId) {
        if (requestList == null || requestList.isEmpty()) {
            return List.of();
        }

        return requestList.stream()
                .map(request -> request.toEntity(tenantId))
                .map(campo -> OcorrenciaDeRelatorioCampoPersonalizadoEntity.from(toSave, campo, tenantId))
                .toList();
    }
}
