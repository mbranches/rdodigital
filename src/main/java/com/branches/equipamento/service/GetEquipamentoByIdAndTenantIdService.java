package com.branches.equipamento.service;

import com.branches.exception.NotFoundException;
import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetEquipamentoByIdAndTenantIdService {
    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoEntity execute(Long id, Long tenantId) {
        return equipamentoRepository.findByIdAndTenantIdAndAtivoIsTrue(id, tenantId)
                .orElseThrow(() -> new NotFoundException("Equipamento não encontrado com o id: " + id));
    }
}
