package com.branches.relatorio.maodeobra.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.relatorio.rdo.repository.MaoDeObraDeAtividadeDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GetMaoDeObraDeAtividadeListByAtividadeIdAndIdInService {
    private final MaoDeObraDeAtividadeDeRelatorioRepository maoDeObraDeAtividadeDeRelatorioRepository;

    public List<MaoDeObraDeAtividadeDeRelatorioEntity> execute(Long atividadeId, Set<Long> ids) {
        List<MaoDeObraDeAtividadeDeRelatorioEntity> response = maoDeObraDeAtividadeDeRelatorioRepository.findAllByIdInAndAtividadeDeRelatorioId(ids, atividadeId);

        if (response.size() != ids.size()) {
            List<Long> missingIds = new ArrayList<>(ids);
            missingIds.removeAll(response.stream().map(MaoDeObraDeAtividadeDeRelatorioEntity::getId).toList());

            throw new NotFoundException("Mão de obra de atividade não encontrada(s) com o(s) id(s): " + missingIds);
        }
        return response;
    }
}
