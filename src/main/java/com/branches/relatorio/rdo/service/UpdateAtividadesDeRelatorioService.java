package com.branches.relatorio.rdo.service;

import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.rdo.dto.request.CampoPersonalizadoRequest;
import com.branches.utils.CalculateHorasTotais;
import com.branches.relatorio.maodeobra.service.GetMaoDeObraListByTenantIdAndIdInAndTypeService;
import com.branches.relatorio.rdo.domain.AtividadeDeRelatorioEntity;
import com.branches.relatorio.rdo.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.relatorio.rdo.domain.RelatorioEntity;
import com.branches.relatorio.rdo.dto.request.AtividadeDeRelatorioRequest;
import com.branches.relatorio.rdo.dto.request.MaoDeObraDeAtividadeRequest;
import com.branches.relatorio.rdo.repository.AtividadeDeRelatorioRepository;
import com.branches.relatorio.rdo.repository.MaoDeObraDeAtividadeDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UpdateAtividadesDeRelatorioService {

    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;
    private final MaoDeObraDeAtividadeDeRelatorioRepository maoDeObraDeAtividadeDeRelatorioRepository;
    private final GetMaoDeObraListByTenantIdAndIdInAndTypeService getMaoDeObraListByTenantIdAndIdInAndTypeService;
    private final GetMaoDeObraDeAtividadeListByAtividadeIdAndIdInService getMaoDeObraDeAtividadeListByAtividadeIdAndIdInService;
    private final GetAtividadeListByRelatorioIdAndIdInService getAtividadeListByRelatorioIdAndIdInService;
    private final CalculateHorasTotais calculateHorasTotais;

    public void execute(List<AtividadeDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId) {
        if (requestList == null || requestList.isEmpty()) {
            atividadeDeRelatorioRepository.removeAllByRelatorioId(relatorio.getId());

            return;
        }

        List<MaoDeObraDeAtividadeRequest> maoDeObraRequests = requestList.stream()
                .flatMap(r -> r.maoDeObra().stream())

                .toList();

        Map<Long, MaoDeObraEntity> maoDeObraEntityMap = getMaoDeObraEntityMap(maoDeObraRequests, tenantId, relatorio);

        var existingAtividades = updateExistingAtividadeDeRelatorio(requestList, relatorio, tenantId, maoDeObraEntityMap);
        var newAtividades = createNewAtividadesDeRelatorio(requestList, relatorio, tenantId, maoDeObraEntityMap);

        List<AtividadeDeRelatorioEntity> atividadesToSave = new ArrayList<>(existingAtividades);
        atividadesToSave.addAll(newAtividades);

        removeAllNotMentioned(existingAtividades, relatorio.getId());

        atividadeDeRelatorioRepository.saveAll(atividadesToSave);
    }

    private void removeAllNotMentioned(List<AtividadeDeRelatorioEntity> existingAtividades, Long id) {
        List<Long> idsToKeep = existingAtividades.stream()
                .map(AtividadeDeRelatorioEntity::getId)
                .toList();

        if (idsToKeep.isEmpty()) {
            atividadeDeRelatorioRepository.removeAllByRelatorioId(id);

            return;
        }

        atividadeDeRelatorioRepository.removeAllByRelatorioIdAndIdNotIn(id, idsToKeep);
    }

    private List<AtividadeDeRelatorioEntity> createNewAtividadesDeRelatorio(List<AtividadeDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        return requestList.stream()
                .filter(r -> r.id() == null)
                .map(request -> {
                    AtividadeDeRelatorioEntity entity = new AtividadeDeRelatorioEntity();

                    entity.setRelatorio(relatorio);
                    entity.setMaoDeObra(new ArrayList<>());
                    setNewFieldsToAtividade(entity, request, tenantId, maoDeObraEntityMap);

                    return entity;
                })
                .toList();
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> updateMaoDeObraDeAtividade(List<MaoDeObraDeAtividadeRequest> requestList, AtividadeDeRelatorioEntity atividade, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        if (requestList == null || requestList.isEmpty()) {
            maoDeObraDeAtividadeDeRelatorioRepository.removeAllByAtividadeDeRelatorioId(atividade.getId());

            return null;
        }

        var existingMaoDeObraList = updateExistingMaoDeObraDeAtividade(requestList, atividade.getId(), maoDeObraEntityMap);

        var newMaoDeObraDeAtividade = createNewMaoDeObraDeAtividade(requestList, atividade, maoDeObraEntityMap);

        List<MaoDeObraDeAtividadeDeRelatorioEntity> maoDeObraDeAtividadeToSave = new ArrayList<>(existingMaoDeObraList);
        maoDeObraDeAtividadeToSave.addAll(newMaoDeObraDeAtividade);

        return maoDeObraDeAtividadeToSave;
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> createNewMaoDeObraDeAtividade(List<MaoDeObraDeAtividadeRequest> requestList, AtividadeDeRelatorioEntity atividade, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        return requestList.stream()
                .filter(r -> r.id() == null)
                .map(request -> {
                    MaoDeObraDeAtividadeDeRelatorioEntity entity = new MaoDeObraDeAtividadeDeRelatorioEntity();

                    entity.setAtividadeDeRelatorio(atividade);
                    MaoDeObraEntity maoDeObraEntity = maoDeObraEntityMap.get(request.maoDeObraId());
                    entity.setMaoDeObra(maoDeObraEntity);
                    entity.setFuncao(maoDeObraEntity.getFuncao());

                    return entity;
                })
                .toList();
    }

    private List<MaoDeObraDeAtividadeDeRelatorioEntity> updateExistingMaoDeObraDeAtividade(List<MaoDeObraDeAtividadeRequest> requestList, Long atividadeId, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        List<Long> ids = requestList.stream()
                .map(MaoDeObraDeAtividadeRequest::id)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, MaoDeObraDeAtividadeRequest> requestMap = requestList.stream()
                .filter(r -> r.id() != null)
                .collect(Collectors.toMap(MaoDeObraDeAtividadeRequest::id, Function.identity()));

        var existingMaoDeObraList = getMaoDeObraDeAtividadeListByAtividadeIdAndIdInService.execute(atividadeId, ids);
        existingMaoDeObraList.forEach(entity -> {
            var request = requestMap.get(entity.getId());

            MaoDeObraEntity maoDeObraEntity = maoDeObraEntityMap.get(request.maoDeObraId());
            entity.setMaoDeObra(maoDeObraEntity);
            entity.setFuncao(maoDeObraEntity.getFuncao());
        });

        return existingMaoDeObraList;
    }

    private List<AtividadeDeRelatorioEntity> updateExistingAtividadeDeRelatorio(List<AtividadeDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        List<Long> ids = requestList.stream()
                .map(AtividadeDeRelatorioRequest::id)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, AtividadeDeRelatorioRequest> requestMap = requestList.stream()
                .filter(r -> r.id() != null)
                .collect(Collectors.toMap(AtividadeDeRelatorioRequest::id, Function.identity()));

        var existingAtividadeList = getAtividadeListByRelatorioIdAndIdInService.execute(relatorio.getId(), ids);
        existingAtividadeList.forEach(entity -> {
            var request = requestMap.get(entity.getId());

            setNewFieldsToAtividade(entity, request, tenantId, maoDeObraEntityMap);
        });

        return existingAtividadeList;
    }

    private void setNewFieldsToAtividade(AtividadeDeRelatorioEntity entity, AtividadeDeRelatorioRequest request, Long tenantId, Map<Long, MaoDeObraEntity> maoDeObraEntityMap) {
        List<CampoPersonalizadoRequest> campoPersonalizadoRequest = request.camposPersonalizados() != null
                ? request.camposPersonalizados()
                : List.of();

        entity.setDescricao(request.descricao());
        entity.setQuantidadeRealizada(request.quantidadeRealizada());
        entity.setUnidadeMedida(request.unidadeMedida());
        entity.setPorcentagemConcluida(request.porcentagemConcluida());
        entity.setStatus(request.status());
        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setTotalHoras(calculateHorasTotais.execute(request.horaInicio(), request.horaFim(), null));
        entity.getCamposPersonalizados().clear();
        entity.setCamposPersonalizados(campoPersonalizadoRequest.stream().map(c -> c.toEntity(tenantId)).toList());
        entity.getMaoDeObra().clear();
        entity.setMaoDeObra(updateMaoDeObraDeAtividade(request.maoDeObra(), entity, maoDeObraEntityMap));
    }

    private Map<Long, MaoDeObraEntity> getMaoDeObraEntityMap(List<MaoDeObraDeAtividadeRequest> requestList, Long tenantId, RelatorioEntity relatorio) {
        Set<Long> maoDeObraIds = requestList.stream()
                .map(MaoDeObraDeAtividadeRequest::maoDeObraId)
                .collect(Collectors.toSet());

        List<MaoDeObraEntity> maoDeObraEntities = getMaoDeObraListByTenantIdAndIdInAndTypeService.execute(tenantId, maoDeObraIds, relatorio.getTipoMaoDeObra());
        return maoDeObraEntities.stream()
                .collect(Collectors.toMap(MaoDeObraEntity::getId, Function.identity()));
    }
}
