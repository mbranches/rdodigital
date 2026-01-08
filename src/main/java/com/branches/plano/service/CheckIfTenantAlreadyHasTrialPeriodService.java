package com.branches.plano.service;

import com.branches.exception.BadRequestException;
import com.branches.plano.repository.PeriodoTesteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckIfTenantAlreadyHasTrialPeriodService {
    private final PeriodoTesteRepository periodoTesteRepository;

    public void execute(Long tenantId) {
        boolean exists = periodoTesteRepository.existsByTenantId(tenantId);

        if (!exists) return;

        throw new BadRequestException("O tenant já iniciou um período de teste anteriormente");
    }
}
