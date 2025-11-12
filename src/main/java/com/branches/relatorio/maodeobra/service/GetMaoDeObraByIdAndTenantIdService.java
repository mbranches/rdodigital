package com.branches.relatorio.maodeobra.service;

import com.branches.exception.NotFoundException;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetMaoDeObraByIdAndTenantIdService {
    private final MaoDeObraRepository maoDeObraRepository;

    public MaoDeObraEntity execute(Long id, Long tenantId) {
        return maoDeObraRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new NotFoundException("Mão de obra com id " + id + " não encontrada para o tenant " + tenantId));
    }
}
