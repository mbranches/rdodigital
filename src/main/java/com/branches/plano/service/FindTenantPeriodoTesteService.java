package com.branches.plano.service;

import com.branches.plano.domain.PeriodoTesteEntity;
import com.branches.plano.repository.PeriodoTesteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FindTenantPeriodoTesteService {
    private final PeriodoTesteRepository periodoTesteRepository;

    public Optional<PeriodoTesteEntity> execute(Long tenantId) {
        return periodoTesteRepository.findByTenantId(tenantId);
    }
}
