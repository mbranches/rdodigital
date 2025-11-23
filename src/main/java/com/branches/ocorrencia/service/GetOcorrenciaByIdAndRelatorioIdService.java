package com.branches.ocorrencia.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.domain.OcorrenciaDeRelatorioEntity;
import com.branches.relatorio.repository.OcorrenciaDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetOcorrenciaByIdAndRelatorioIdService {
    private final OcorrenciaDeRelatorioRepository ocorrenciaDeRelatorioRepository;

    public OcorrenciaDeRelatorioEntity execute(Long id, Long relatorioId) {
        return ocorrenciaDeRelatorioRepository.findByIdAndRelatorioId(id, relatorioId)
                .orElseThrow(() -> new NotFoundException("Ocorrência de relatório não encontrada com o id: %d".formatted(id)));
    }
}
