package com.branches.assinaturadeplano.service;

import com.branches.assinaturadeplano.domain.AssinaturaDePlanoEntity;
import com.branches.assinaturadeplano.domain.enums.AssinaturaStatus;
import com.branches.assinaturadeplano.repository.AssinaturaDePlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FindAssinaturaCorrenteByTenantIdService {
    private final AssinaturaDePlanoRepository assinaturaDePlanoRepository;

    public Optional<AssinaturaDePlanoEntity> execute(Long tenantId) {
        return assinaturaDePlanoRepository.findByStatusInAndTenantId(
                AssinaturaStatus.getStatusListOfAssinaturaCorrente(),
                tenantId
        );
    }
}
