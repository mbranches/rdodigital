package com.branches.relatorio.service;

import com.branches.relatorio.domain.MaterialDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.relatorio.dto.request.MaterialDeRelatorioRequest;
import com.branches.relatorio.repository.MaterialDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UpdateMateriaisDeRelatorioService {

    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;
    private final GetMaterialListByRelatorioIdAndIdInService getMaterialListByRelatorioIdAndIdInService;

    public void execute(List<MaterialDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId) {
        if (requestList == null || requestList.isEmpty()) {
            materialDeRelatorioRepository.removeAllByRelatorioId(relatorio.getId());

            return;
        }

        List<MaterialDeRelatorioEntity> updatedMaterials = updateExistingMaterials(requestList,  relatorio, tenantId);
        List<MaterialDeRelatorioEntity> newMaterials = createNewMaterials(requestList, relatorio, tenantId);

        List<MaterialDeRelatorioEntity> materialsToSave = new ArrayList<>(updatedMaterials);
        materialsToSave.addAll(newMaterials);

        removeAllNotMentioned(updatedMaterials, relatorio.getId());

        materialDeRelatorioRepository.saveAll(materialsToSave);
    }

    private void removeAllNotMentioned(List<MaterialDeRelatorioEntity> updatedMaterials, Long relatorioId) {
        List<Long> idsToKeep = updatedMaterials.stream()
                .map(MaterialDeRelatorioEntity::getId)
                .toList();

        if (idsToKeep.isEmpty()) {
            materialDeRelatorioRepository.removeAllByRelatorioId(relatorioId);

            return;
        }

        materialDeRelatorioRepository.removeAllByIdNotInAndRelatorioId(idsToKeep, relatorioId);
    }

    private List<MaterialDeRelatorioEntity> createNewMaterials(List<MaterialDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId) {
        return requestList.stream()
                .filter(r -> r.id() == null)
                .map(request -> {
                    MaterialDeRelatorioEntity entity = new MaterialDeRelatorioEntity();

                    entity.setRelatorio(relatorio);
                    entity.setDescricao(request.descricao());
                    entity.setQuantidade(request.quantidade());
                    entity.setTipoMaterial(request.tipoMaterial());

                    return entity;
                })
                .toList();
    }

    private List<MaterialDeRelatorioEntity> updateExistingMaterials(List<MaterialDeRelatorioRequest> requestList, RelatorioEntity relatorio, Long tenantId) {
        Set<Long> ids = requestList.stream()
                .map(MaterialDeRelatorioRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, MaterialDeRelatorioRequest> requestMap = requestList.stream()
                .filter(r -> r.id() != null)
                .collect(Collectors.toMap(MaterialDeRelatorioRequest::id, Function.identity()));

        var existingMaterials = getMaterialListByRelatorioIdAndIdInService.execute(relatorio.getId(), ids);
        existingMaterials.forEach(material -> {
            var request = requestMap.get(material.getId());
            material.setDescricao(request.descricao());
            material.setQuantidade(request.quantidade());
            material.setTipoMaterial(request.tipoMaterial());
        });

        return existingMaterials;
    }
}
