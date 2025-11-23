package com.branches.maodeobra.service;

import com.branches.exception.NotFoundException;
import com.branches.maodeobra.domain.MaoDeObraDeRelatorioEntity;
import com.branches.relatorio.repository.MaoDeObraDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetMaoDeObraDeRelatorioByIdAndRelatorioId {
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;

    public MaoDeObraDeRelatorioEntity execute(Long id, Long relatorioId) {
        return maoDeObraDeRelatorioRepository.findByIdAndRelatorioId(id, relatorioId)
                .orElseThrow(() -> new NotFoundException("Mão de obra de relatório não encontrada com o id: %d".formatted(id)));
    }
}
