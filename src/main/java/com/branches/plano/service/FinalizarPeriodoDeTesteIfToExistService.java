package com.branches.plano.service;

import com.branches.plano.domain.PeriodoTesteEntity;
import com.branches.plano.repository.PeriodoTesteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FinalizarPeriodoDeTesteIfToExistService {
    private final PeriodoTesteRepository periodoTesteRepository;

    public void execute(Long tenantId) {
        PeriodoTesteEntity entity = periodoTesteRepository.findByTenantId(tenantId)
                .orElse(null);

        if (entity == null) return;

        entity.finalizar();

        periodoTesteRepository.save(entity);
    }
}
