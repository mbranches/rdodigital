package com.branches.assinatura.service;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.repository.AssinaturaRepository;
import com.branches.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.branches.assinatura.domain.enums.AssinaturaStatus.*;

@RequiredArgsConstructor
@Service
public class GetAssinaturaActiveByTenantIdService {
    private final AssinaturaRepository assinaturaRepository;

    public AssinaturaEntity execute(Long tenantId) {
        return assinaturaRepository.findByStatusInAndTenantId(List.of(ATIVO, VENCIDO), tenantId)
                .orElseThrow(() -> new NotFoundException("Assinatura ativa não encontrada para o tenant"));
    }
}
