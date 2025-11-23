package com.branches.ocorrencia.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.service.GetObraByIdAndTenantIdService;
import com.branches.relatorio.dto.request.CampoPersonalizadoRequest;
import com.branches.relatorio.service.CheckIfUserHasAccessToEditRelatorioService;
import com.branches.relatorio.service.GetRelatorioByIdExternoAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.CalculateHorasTotais;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.ocorrencia.dto.request.UpdateOcorrenciaDeRelatorioRequest;
import com.branches.relatorio.repository.OcorrenciaDeRelatorioRepository;
import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.utils.ValidateHoraInicioAndHoraFim;
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
    private final ValidateHoraInicioAndHoraFim validateHoraInicioAndHoraFim;
    private final CalculateHorasTotais calculateHorasTotais;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final CheckIfUserHasAccessToEditRelatorioService checkIfUserHasAccessToEditRelatorioService;
    private final GetRelatorioByIdExternoAndTenantIdService getRelatorioByIdExternoAndTenantIdService;
    private final GetObraByIdAndTenantIdService getObraByIdAndTenantIdService;

    public void execute(UpdateOcorrenciaDeRelatorioRequest request, Long id, String relatorioExternalId, String tenantExternalId, List<UserTenantEntity> userTenants) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity userTenant = getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfUserHasAccessToEditRelatorioService.execute(userTenant);

        RelatorioEntity relatorio = getRelatorioByIdExternoAndTenantIdService.execute(relatorioExternalId, tenantId);

        checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrencia(relatorio, tenantId);

        checkIfUserCanViewOcorrencias(userTenant);

        OcorrenciaDeRelatorioEntity entity = getOcorrenciaByIdAndRelatorioIdService.execute(id, relatorio.getId());

        entity.setDescricao(request.descricao());

        validateHoraInicioAndHoraFim.execute(request.horaInicio(), request.horaFim());
        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setTotalHoras(calculateHorasTotais.execute(request.horaInicio(), request.horaFim(), null));

        List<TipoDeOcorrenciaEntity> tiposDeOcorrencia = getTiposDeOcorrenciaList(request.tiposOcorrenciaIds(), tenantId);
        entity.getTiposDeOcorrencia().clear();
        entity.getTiposDeOcorrencia().addAll(tiposDeOcorrencia);

        List<CampoPersonalizadoRequest> campoPersonalizadoRequest = request.camposPersonalizados() != null
                ? request.camposPersonalizados()
                : List.of();
        entity.getCamposPersonalizados().clear();
        entity.getCamposPersonalizados().addAll(
                campoPersonalizadoRequest.stream().map(c -> c.toEntity(tenantId)).toList()
        );

        ocorrenciaDeRelatorioRepository.save(entity);
    }

    private List<TipoDeOcorrenciaEntity> getTiposDeOcorrenciaList(List<Long> ids, Long tenantId) {
        return ids != null && !ids.isEmpty() ? getTiposDeOcorrenciaByTenantIdAndIdInService.execute(tenantId, new HashSet<>(ids))
                : List.of();
    }

    private void checkIfUserCanViewOcorrencias(UserTenantEntity userTenant) {
        if (userTenant.getAuthorities().getItensDeRelatorio().getOcorrencias()) return;

        throw new ForbiddenException();
    }

    private void checkIfConfiguracaoDeRelatorioDaObraPermiteOcorrencia(RelatorioEntity relatorio, Long tenantId) {
        ObraEntity obra = getObraByIdAndTenantIdService.execute(relatorio.getObraId(), tenantId);

        if (obra.getConfiguracaoRelatorios().getShowOcorrencias()) return;

        throw new ForbiddenException();
    }
}
