package com.branches.assinaturadeplano.service;

import com.branches.assinaturadeplano.domain.AssinaturaDePlanoEntity;
import com.branches.assinaturadeplano.repository.AssinaturaDePlanoRepository;
import com.branches.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetAssinaturaByStripeIdService {
    private final AssinaturaDePlanoRepository assinaturaDePlanoRepository;

    public AssinaturaDePlanoEntity execute(String stripeId) {
        return assinaturaDePlanoRepository.findByStripeSubscriptionId(stripeId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para o Stripe ID: " + stripeId));
    }
}
