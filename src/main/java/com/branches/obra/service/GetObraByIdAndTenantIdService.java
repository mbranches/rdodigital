package com.branches.obra.service;

import com.branches.exception.NotFoundException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.repository.ObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetObraByIdAndTenantIdService {
    private final ObraRepository obraRepository;

    public ObraEntity execute(Long obraId, Long tenantId) {
        return obraRepository.findByIdAndTenantId(obraId, tenantId)
                .orElseThrow(() -> new NotFoundException("Obra não encontrada para o tenant informado"));
    }
}
