package com.branches.relatorio.rdo.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.rdo.domain.EquipamentoDeRelatorioEntity;
import com.branches.relatorio.rdo.repository.EquipamentoDeRelatorioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetEquipamentoDeRelatorioByIdAndRelatorioIdService {
    private final EquipamentoDeRelatorioRepository equipamentoDeRelatorioRepository;

    public EquipamentoDeRelatorioEntity execute(Long id, Long relatorioId) {
        return equipamentoDeRelatorioRepository.findByIdAndRelatorioId(id, relatorioId)
                .orElseThrow(() -> new NotFoundException("Equipamento de relatório não encontrado com o ID: " + id));
    }
}
