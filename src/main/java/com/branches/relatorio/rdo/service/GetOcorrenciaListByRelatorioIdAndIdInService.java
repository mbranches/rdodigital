package com.branches.relatorio.rdo.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.OcorrenciaDeRelatorioEntity;
import com.branches.relatorio.rdo.repository.OcorrenciaDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GetOcorrenciaListByRelatorioIdAndIdInService {
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;

    public List<OcorrenciaDeRelatorioEntity> execute(Long relatorioId, Set<Long> ids) {
        var response = ocorrenciaDeRelatorioRepository.findAllByIdInAndRelatorioId(ids, relatorioId);

        if (response.size() != ids.size()) {
            List<Long> missingIds = new ArrayList<>(ids);
            missingIds.removeAll(response.stream().map(OcorrenciaDeRelatorioEntity::getId).toList());

            throw new NotFoundException("Ocorrencia(s) de relatorio nao encontrado(s) para o(s) id(s): " + missingIds);
        }

        return response;
    }
}
