package com.branches.plano.service;

import com.branches.exception.NotFoundException;
import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetPlanoByStripeIdService {
    private final PlanoRepository planoRepository;

    public PlanoEntity execute(String stripeId) {
        return planoRepository.findByStripePriceId(stripeId)
                .orElseThrow(() -> new NotFoundException("Plano não encontrado para o Stripe ID: " + stripeId));
    }
}
