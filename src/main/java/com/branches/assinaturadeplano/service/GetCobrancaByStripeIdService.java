package com.branches.assinaturadeplano.service;

import com.branches.assinaturadeplano.domain.CobrancaEntity;
import com.branches.assinaturadeplano.repository.CobrancaRepository;
import com.branches.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetCobrancaByStripeIdService {
    private final CobrancaRepository cobrancaRepository;

    public CobrancaEntity execute(String stripeInvoiceId) {
        return cobrancaRepository.findByStripeInvoiceId(stripeInvoiceId)
                .orElseThrow(() -> new NotFoundException("Cobranca não encontrada para o Stripe Invoice ID: " + stripeInvoiceId));
    }
}
