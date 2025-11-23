package com.branches.equipamento.service;

import com.branches.exception.NotFoundException;
import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.repository.EquipamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GetEquipamentoListByTenantIdAndIdInService {
    private final EquipamentoRepository equipamentoRepository;

    public List<EquipamentoEntity> execute(Long tenantId, Set<Long> equipamentosIds) {
        List<EquipamentoEntity> response = equipamentoRepository.findAllByIdInAndTenantIdAndAtivoIsTrue(equipamentosIds, tenantId);

        if (response.size() != equipamentosIds.size()) {
            List<Long> missingIds = new ArrayList<>(equipamentosIds);
            missingIds.removeAll(response.stream().map(EquipamentoEntity::getId).toList());

            throw new NotFoundException("Equipamento(s) não encontrado(s) com id(s): " + missingIds);
        }

        return response;
    }
}
