package com.branches.relatorio.rdo.service;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.domain.enums.PresencaMaoDeObra;
import com.branches.utils.CalculateHorasTotais;
import com.branches.relatorio.maodeobra.service.GetMaoDeObraListByTenantIdAndIdInAndTypeService;
import com.branches.relatorio.rdo.domain.MaoDeObraDeRelatorioEntity;
import com.branches.relatorio.rdo.domain.RelatorioEntity;
import com.branches.relatorio.rdo.dto.request.MaoDeObraDeRelatorioRequest;
import com.branches.relatorio.rdo.repository.MaoDeObraDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class UpdateMaoDeObraDeRelatorioService {
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;
    private final GetMaoDeObraListByTenantIdAndIdInAndTypeService getMaoDeObraListByTenantIdAndIdInAndTypeService;
    private final GetMaoDeObraDeRelatorioListByRelatorioIdAndIdInService getMaoDeObraDeRelatorioListByRelatorioIdAndIdInService;
    private final CalculateHorasTotais calculateHorasTotais;

    public void execute(List<MaoDeObraDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId) {
        if(requestList == null || requestList.isEmpty()) {
            maoDeObraDeRelatorioRepository.removeAllByRelatorioId(relatorio.getId());

            return;
        }

        TipoMaoDeObra tipoMaoDeObra = relatorio.getTipoMaoDeObra();

        Map<Long, MaoDeObraEntity> maoDeObraEntityMap = getMaoDeObraMap(requestList, tenantId, tipoMaoDeObra);

        List<MaoDeObraDeRelatorioEntity> existingMaoDeObraList = updateExistingMaoDeObraDeRelatorio(requestList, relatorio, maoDeObraEntityMap, tipoMaoDeObra);

        List<MaoDeObraDeRelatorioEntity> newMaoDeObraDeRelatorio = createNewMaoDeObraDeRelatorio(requestList, relatorio, maoDeObraEntityMap, tipoMaoDeObra);

        List<MaoDeObraDeRelatorioEntity> maoDeObraDeRelatorioToSave = new ArrayList<>(existingMaoDeObraList);
        maoDeObraDeRelatorioToSave.addAll(newMaoDeObraDeRelatorio);

        removeAllNotMentioned(existingMaoDeObraList,  relatorio.getId());

        maoDeObraDeRelatorioRepository.saveAll(maoDeObraDeRelatorioToSave);
    }

    private List<MaoDeObraDeRelatorioEntity> createNewMaoDeObraDeRelatorio(List<MaoDeObraDeRelatorioRequest> requestList, RelatorioEntity relatorio, Map<Long, MaoDeObraEntity> maoDeObraEntityMap, TipoMaoDeObra tipoMaoDeObra) {
        return requestList.stream()
                .filter(r -> r.id() == null)
                .map(request -> {
                    MaoDeObraDeRelatorioEntity entity = new MaoDeObraDeRelatorioEntity();

                    entity.setRelatorio(relatorio);
                    setNewFieldsToMaoDeObraDeRelatorioEntity(entity, request, maoDeObraEntityMap.get(request.maoDeObraId()), tipoMaoDeObra);

                    return entity;
                })
                .toList();
    }

    private List<MaoDeObraDeRelatorioEntity> updateExistingMaoDeObraDeRelatorio(List<MaoDeObraDeRelatorioRequest> requestList, RelatorioEntity relatorio, Map<Long, MaoDeObraEntity> maoDeObraEntityMap, TipoMaoDeObra tipoMaoDeObra) {
        Set<Long> ids = requestList.stream()
                .map(MaoDeObraDeRelatorioRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, MaoDeObraDeRelatorioRequest> requestMap = requestList.stream()
                .filter(r -> r.id() != null)
                .collect(Collectors.toMap(MaoDeObraDeRelatorioRequest::id, Function.identity()));

        var existingMaoDeObraList = getMaoDeObraDeRelatorioListByRelatorioIdAndIdInService.execute(relatorio.getId(), ids);
        existingMaoDeObraList.forEach(entity -> {
            var request = requestMap.get(entity.getId());

            setNewFieldsToMaoDeObraDeRelatorioEntity(entity, request, maoDeObraEntityMap.get(request.maoDeObraId()), tipoMaoDeObra);
        });

        return existingMaoDeObraList;
    }

    private void removeAllNotMentioned(List<MaoDeObraDeRelatorioEntity> existingMaoDeObraList, Long relatorioId) {
        if (existingMaoDeObraList.isEmpty()) {
            maoDeObraDeRelatorioRepository.removeAllByRelatorioId(relatorioId);

            return;
        }

        maoDeObraDeRelatorioRepository.removeAllByIdNotInAndRelatorioId(
                existingMaoDeObraList.stream()
                        .map(MaoDeObraDeRelatorioEntity::getId)
                        .toList(),
                relatorioId
        );
    }

    private void setNewFieldsToMaoDeObraDeRelatorioEntity(MaoDeObraDeRelatorioEntity entity, MaoDeObraDeRelatorioRequest request, MaoDeObraEntity maoDeObra, TipoMaoDeObra tipoMaoDeObra) {
        entity.setPresenca(request.presenca());
        entity.setMaoDeObra(maoDeObra);
        entity.setFuncao(maoDeObra.getFuncao());

        if (tipoMaoDeObra.equals(TipoMaoDeObra.GENERICA) || !request.presenca().equals(PresencaMaoDeObra.PRESENTE)) return;

        entity.setHoraInicio(request.horaInicio());
        entity.setHoraFim(request.horaFim());
        entity.setHorasIntervalo(request.horasIntervalo());
        LocalTime horasTrabalhadas = calculateHorasTotais.execute(request.horaInicio(), request.horaFim(), request.horasIntervalo());
        entity.setHorasTrabalhadas(horasTrabalhadas);
    }

    private Map<Long, MaoDeObraEntity> getMaoDeObraMap(List<MaoDeObraDeRelatorioRequest> requestList, Long tenantId, TipoMaoDeObra tipoMaoDeObra) {
        Set<Long> maoDeObraIds = requestList.stream()
                .map(MaoDeObraDeRelatorioRequest::maoDeObraId)
                .collect(Collectors.toSet());

        List<MaoDeObraEntity> maoDeObraEntities = getMaoDeObraListByTenantIdAndIdInAndTypeService.execute(tenantId, maoDeObraIds, tipoMaoDeObra);
        return maoDeObraEntities.stream()
                .collect(Collectors.toMap(MaoDeObraEntity::getId, Function.identity()));
    }
}
