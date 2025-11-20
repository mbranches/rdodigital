package com.branches.relatorio.rdo.service;

import com.branches.relatorio.rdo.dto.request.CampoPersonalizadoRequest;
import com.branches.utils.CalculateHorasTotais;
import com.branches.relatorio.rdo.domain.OcorrenciaDeRelatorioEntity;
import com.branches.relatorio.rdo.domain.RelatorioEntity;
import com.branches.relatorio.rdo.dto.request.OcorrenciaDeRelatorioRequest;
import com.branches.relatorio.rdo.repository.OcorrenciaDeRelatorioRepository;
import com.branches.relatorio.tipodeocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.relatorio.tipodeocorrencia.service.GetTiposDeOcorrenciaByTenantIdAndIdInService;
import com.branches.utils.ValidateHoraInicioAndHoraFim;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class UpdateOcorrenciasDeRelatorioService {
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;
    private final GetOcorrenciaListByRelatorioIdAndIdInService getOcorrenciaListByRelatorioIdAndIdInService;
    private final GetTiposDeOcorrenciaByTenantIdAndIdInService getTiposDeOcorrenciaByTenantIdAndIdInService;
    private final ValidateHoraInicioAndHoraFim validateHoraInicioAndHoraFim;
    private final CalculateHorasTotais calculateHorasTotais;

    public void execute(List<OcorrenciaDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId) {
        if (requestList == null || requestList.isEmpty()) {
            ocorrenciaDeRelatorioRepository.removeAllByRelatorioId(relatorio.getId());

            return;
        }

        Map<Long, TipoDeOcorrenciaEntity> tiposDeOcorrenciaMap = getTiposDeOcorrenciaMap(tenantId, requestList);

        List<OcorrenciaDeRelatorioEntity> updatedOcorrencias = updateExistingOcorrencias(requestList, relatorio, tenantId, tiposDeOcorrenciaMap);
        List<OcorrenciaDeRelatorioEntity> newOcorrencias = createNewOcorrencias(requestList, relatorio, tenantId, tiposDeOcorrenciaMap);

        List<OcorrenciaDeRelatorioEntity> ocorrenciasToSave = new ArrayList<>(updatedOcorrencias);
        ocorrenciasToSave.addAll(newOcorrencias);

        removeAllNotMentioned(updatedOcorrencias, relatorio.getId());

        ocorrenciaDeRelatorioRepository.saveAll(ocorrenciasToSave);
    }

    private void removeAllNotMentioned(List<OcorrenciaDeRelatorioEntity> updatedOcorrencias, Long relatorioId) {
        List<Long> ids = updatedOcorrencias.stream()
                .map(OcorrenciaDeRelatorioEntity::getId)
                .filter(Objects::nonNull)
                .toList();

        if (ids.isEmpty()) {
            ocorrenciaDeRelatorioRepository.removeAllByRelatorioId(relatorioId);

            return;
        }

        ocorrenciaDeRelatorioRepository.removeAllByIdNotInAndRelatorioId(ids, relatorioId);
    }

    private List<OcorrenciaDeRelatorioEntity> createNewOcorrencias(List<OcorrenciaDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId, Map<Long, TipoDeOcorrenciaEntity> tiposDeOcorrenciaMap) {
        return requestList.stream()
                .filter(request -> request.id() == null)
                .map(request -> {
                    OcorrenciaDeRelatorioEntity ocorrencia = new OcorrenciaDeRelatorioEntity();
                    ocorrencia.setRelatorio(relatorio);
                    setNewFields(ocorrencia, request, tiposDeOcorrenciaMap, tenantId);
                    return ocorrencia;
                })
                .toList();
    }

    private List<OcorrenciaDeRelatorioEntity> updateExistingOcorrencias(List<OcorrenciaDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId, Map<Long, TipoDeOcorrenciaEntity> tiposDeOcorrenciaMap) {
        List<Long> ids = requestList.stream()
                .map(OcorrenciaDeRelatorioRequest::id)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, OcorrenciaDeRelatorioRequest> requestMap = requestList.stream()
                .filter(request -> request.id() != null)
                .collect(Collectors.toMap(OcorrenciaDeRelatorioRequest::id, Function.identity()));

        List<OcorrenciaDeRelatorioEntity> existingOcorrencias = getOcorrenciaListByRelatorioIdAndIdInService.execute(relatorio.getId(), ids);

        existingOcorrencias.forEach(ocorrencia -> {
            OcorrenciaDeRelatorioRequest request = requestMap.get(ocorrencia.getId());

            setNewFields(ocorrencia, request, tiposDeOcorrenciaMap, tenantId);
        });

        return existingOcorrencias;
    }

    private void setNewFields(OcorrenciaDeRelatorioEntity ocorrencia, OcorrenciaDeRelatorioRequest request,
                              Map<Long, TipoDeOcorrenciaEntity> tiposDeOcorrenciaMap,
                              Long tenantId) {

        ocorrencia.setDescricao(request.descricao());

        List<TipoDeOcorrenciaEntity> currentTipos = request.tiposOcorrenciaIds() != null && !request.tiposOcorrenciaIds().isEmpty()
                ? request.tiposOcorrenciaIds().stream()
                .map(tiposDeOcorrenciaMap::get)
                .toList()
                : List.of();

        ocorrencia.getTiposDeOcorrencia().clear();
        ocorrencia.getTiposDeOcorrencia().addAll(currentTipos);

        List<CampoPersonalizadoRequest> campoPersonalizadoRequest = request.camposPersonalizados() != null
                ? request.camposPersonalizados()
                : List.of();

        validateHoraInicioAndHoraFim.execute(request.horaInicio(), request.horaFim());
        ocorrencia.setHoraInicio(request.horaInicio());
        ocorrencia.setHoraFim(request.horaFim());
        ocorrencia.setTotalHoras(calculateHorasTotais.execute(request.horaInicio(), request.horaFim(), null));
        ocorrencia.getCamposPersonalizados().clear();
        ocorrencia.getCamposPersonalizados().addAll(
                campoPersonalizadoRequest.stream().map(c -> c.toEntity(tenantId)).toList()
        );
    }

    private Map<Long, TipoDeOcorrenciaEntity> getTiposDeOcorrenciaMap(Long tenantId, List<OcorrenciaDeRelatorioRequest> requestList) {
        Set<Long> tipoDeOcorrenciaIds = requestList.stream()
                .filter(request -> request.tiposOcorrenciaIds() != null)
                .flatMap(request -> request.tiposOcorrenciaIds().stream())
                .collect(Collectors.toSet());

        List<TipoDeOcorrenciaEntity> tiposDeOcorrencia = getTiposDeOcorrenciaByTenantIdAndIdInService.execute(tenantId, tipoDeOcorrenciaIds);
        return tiposDeOcorrencia.stream()
                .collect(Collectors.toMap(TipoDeOcorrenciaEntity::getId, Function.identity()));
    }
}
