package com.branches.obra.service;

import com.branches.exception.NotFoundException;
import com.branches.obra.repository.ObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetObraIdByIdExternoService {
    private final ObraRepository obraRepository;

    public Long execute(String obraExternalId, Long tenantId) {
        return obraRepository.findIdByIdExternoAndTenantId(obraExternalId, tenantId)
                .orElseThrow(() -> new NotFoundException("Obra não encontrada com o id: " + obraExternalId));
    }
}
