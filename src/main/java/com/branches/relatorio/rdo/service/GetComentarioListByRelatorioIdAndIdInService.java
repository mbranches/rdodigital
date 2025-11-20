package com.branches.relatorio.rdo.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.ComentarioDeRelatorioEntity;
import com.branches.relatorio.rdo.repository.ComentarioDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GetComentarioListByRelatorioIdAndIdInService {
    private final ComentarioDeRelatorioRepository comentarioDeRelatorioRepository;

    public List<ComentarioDeRelatorioEntity> execute(Long relatorioId, Set<Long> ids) {
        List<ComentarioDeRelatorioEntity> response = comentarioDeRelatorioRepository.findAllByIdInAndRelatorioId(ids, relatorioId);

        if (response.size() != ids.size()) {
            List<Long> missingIds = new ArrayList<>(ids);
            missingIds.removeAll(response.stream().map(ComentarioDeRelatorioEntity::getId).toList());

            throw new NotFoundException("Comentario(s) de relatorio nao encontrado(s) para o(s) id(s): " + missingIds);
        }

        return response;
    }
}
