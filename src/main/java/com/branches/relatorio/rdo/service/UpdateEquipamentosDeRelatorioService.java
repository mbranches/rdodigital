package com.branches.relatorio.rdo.service;

import com.branches.relatorio.equipamento.domain.EquipamentoEntity;
import com.branches.relatorio.equipamento.service.GetEquipamentoListByTenantIdAndIdInService;
import com.branches.relatorio.rdo.domain.EquipamentoDeRelatorioEntity;
import com.branches.relatorio.rdo.domain.RelatorioEntity;
import com.branches.relatorio.rdo.dto.request.EquipamentoDeRelatorioRequest;
import com.branches.relatorio.rdo.repository.EquipamentoDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class UpdateEquipamentosDeRelatorioService {

    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;
    private final GetEquipamentoListByTenantIdAndIdInService getEquipamentoListByTenantIdAndIdInService;
    private final GetEquipamentoDeRelatorioListByRelatorioIdAndIdInService getEquipamentoDeRelatorioListByRelatorioIdAndIdInService;

    public void execute(List<EquipamentoDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId) {
        if(requestList == null || requestList.isEmpty()) {
            equipamentoDeRelatorioRepository.removeAllByRelatorioId(relatorio.getId());

            return;
        }

        Map<Long, EquipamentoEntity> equipamentoMap = getEquipamentoMap(tenantId, requestList);

        List<EquipamentoDeRelatorioEntity> existingEquipamentoList = updateExistingEquipamentoDeRelatorio(requestList, relatorio, equipamentoMap);

        List<EquipamentoDeRelatorioEntity> newEquipamentoDeRelatorio = createNewEquipamentoDeRelatorio(requestList, relatorio, equipamentoMap);

        List<EquipamentoDeRelatorioEntity> equipamentoDeRelatorioToSave = new ArrayList<>(existingEquipamentoList);
        equipamentoDeRelatorioToSave.addAll(newEquipamentoDeRelatorio);

        removeAllNotMentionedEquipamento(existingEquipamentoList, relatorio.getId());

        equipamentoDeRelatorioRepository.saveAll(equipamentoDeRelatorioToSave);
    }

    private void removeAllNotMentionedEquipamento(List<EquipamentoDeRelatorioEntity> existingEquipamentoList, Long relatorioId) {
        if (existingEquipamentoList.isEmpty()) {
            equipamentoDeRelatorioRepository.removeAllByRelatorioId(relatorioId);

            return;
        }

        equipamentoDeRelatorioRepository.removeAllByIdNotInAndRelatorioId(
                existingEquipamentoList.stream()
                        .map(EquipamentoDeRelatorioEntity::getId)
                        .toList(),
                relatorioId
        );
    }

    private List<EquipamentoDeRelatorioEntity> createNewEquipamentoDeRelatorio(List<EquipamentoDeRelatorioRequest> requestList, RelatorioEntity relatorio, Map<Long, EquipamentoEntity> equipamentoEntityMap) {
        return requestList.stream()
                .filter(r -> r.id() == null)
                .map(request -> {
                    EquipamentoDeRelatorioEntity entity = new EquipamentoDeRelatorioEntity();

                    entity.setRelatorio(relatorio);
                    entity.setEquipamento(equipamentoEntityMap.get(request.equipamentoId()));
                    entity.setQuantidade(request.quantidade());

                    return entity;
                })
                .toList();
    }

    private List<EquipamentoDeRelatorioEntity> updateExistingEquipamentoDeRelatorio(List<EquipamentoDeRelatorioRequest> requestList, RelatorioEntity relatorio, Map<Long, EquipamentoEntity> equipamentoEntityMap) {
        List<Long> ids = requestList.stream()
                .map(EquipamentoDeRelatorioRequest::id)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, EquipamentoDeRelatorioRequest> requestMap = requestList.stream()
                .filter(r -> r.id() != null)
                .collect(Collectors.toMap(EquipamentoDeRelatorioRequest::id, Function.identity()));

        var existingEquipamentoList = getEquipamentoDeRelatorioListByRelatorioIdAndIdInService.execute(relatorio.getId(), ids);

        existingEquipamentoList.forEach(entity -> {
            var request = requestMap.get(entity.getId());

            entity.setEquipamento(equipamentoEntityMap.get(request.equipamentoId()));
            entity.setQuantidade(request.quantidade());
        });

        return existingEquipamentoList;
    }

    private Map<Long, EquipamentoEntity> getEquipamentoMap(Long tenantId, List<EquipamentoDeRelatorioRequest> requestList) {
        Set<Long> equipamentoIds = requestList.stream()
                .map(EquipamentoDeRelatorioRequest::equipamentoId)
                .collect(Collectors.toSet());

        List<EquipamentoEntity> equipamentosEntities = getEquipamentoListByTenantIdAndIdInService.execute(tenantId, equipamentoIds);
        return equipamentosEntities.stream()
                .collect(Collectors.toMap(EquipamentoEntity::getId, Function.identity()));
    }
}
