package com.branches.plano.service;

import com.branches.assinaturadeplano.domain.enums.AssinaturaStatus;
import com.branches.assinaturadeplano.repository.AssinaturaDePlanoRepository;
import com.branches.exception.BadRequestException;
import com.branches.plano.repository.PeriodoTesteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckIfTenantAlreadyCanIniciarPeriodoTesteService {
    private final PeriodoTesteRepository periodoTesteRepository;
    private final AssinaturaDePlanoRepository assinaturaDePlanoRepository;

    public void execute(Long tenantId) {
        boolean alreadyStartedTrialPeriod = periodoTesteRepository.existsByTenantId(tenantId);

        if (alreadyStartedTrialPeriod) {
            throw new BadRequestException("O tenant já iniciou um período de teste anteriormente");
        }

        boolean alreadyHasActiveSubscription = assinaturaDePlanoRepository.existsByStatusIn(AssinaturaStatus.getStatusListThatAlreadyHaveActivePlan());

        if (alreadyHasActiveSubscription) {
            throw new BadRequestException("O tenant já possuiu uma assinatura ativa");
        }
    }
}
