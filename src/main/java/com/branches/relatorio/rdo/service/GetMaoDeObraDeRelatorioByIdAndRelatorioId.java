package com.branches.relatorio.rdo.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.MaoDeObraDeRelatorioEntity;
import com.branches.relatorio.rdo.repository.MaoDeObraDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GetMaoDeObraDeRelatorioByIdAndRelatorioId {
    private final MaoDeObraDeRelatorioRepository maoDeObraDeRelatorioRepository;

    public MaoDeObraDeRelatorioEntity execute(Long id, Long relatorioId) {
        return maoDeObraDeRelatorioRepository.findByIdAndRelatorioId(id, relatorioId)
                .orElseThrow(() -> new NotFoundException("Mão de obra de relatório não encontrada com o id: %d".formatted(id)));
    }
}
