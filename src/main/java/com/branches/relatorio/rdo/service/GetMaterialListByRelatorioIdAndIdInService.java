package com.branches.relatorio.rdo.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.MaterialDeRelatorioEntity;
import com.branches.relatorio.rdo.repository.MaterialDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GetMaterialListByRelatorioIdAndIdInService {
    private final MaterialDeRelatorioRepository materialDeRelatorioRepository;

    public List<MaterialDeRelatorioEntity> execute(Long relatorioId, Set<Long> ids) {
        var response = materialDeRelatorioRepository.findAllByIdInAndRelatorioId(ids, relatorioId);

        if (response.size() != ids.size()) {
            List<Long> missingIds = new ArrayList<>(ids);
            missingIds.removeAll(response.stream().map(MaterialDeRelatorioEntity::getId).toList());

            throw new NotFoundException("Material(is) de relatorio nao encontrado(s) para o(s) id(s): " + missingIds);
        }

        return response;
    }
}
