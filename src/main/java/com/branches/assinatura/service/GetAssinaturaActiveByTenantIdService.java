package com.branches.assinatura.service;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.domain.enums.AssinaturaStatus;
import com.branches.assinatura.repository.AssinaturaRepository;
import com.branches.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetAssinaturaActiveByTenantIdService {
    private final AssinaturaRepository assinaturaRepository;

    public AssinaturaEntity execute(Long tenantId) {
        return assinaturaRepository.findByStatusAndTenantId(AssinaturaStatus.ATIVO, tenantId)
                .orElseThrow(() -> new NotFoundException("Assinatura ativa não encontrada para o tenant"));
    }
}
