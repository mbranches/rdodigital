package com.branches.atividade.service;

import com.branches.exception.NotFoundException;
import com.branches.atividade.domain.AtividadeDeRelatorioEntity;
import com.branches.relatorio.repository.AtividadeDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetAtividadeDeRelatorioByIdAndRelatorioIdService {
    private final AtividadeDeRelatorioRepository atividadeDeRelatorioRepository;

    public AtividadeDeRelatorioEntity execute(Long id, Long relatorioId) {
        return atividadeDeRelatorioRepository.findByIdAndRelatorioId(id, relatorioId)
                .orElseThrow(() -> new NotFoundException("Atividade de relatório não encontrada com o id: %d".formatted(id)));
    }
}
