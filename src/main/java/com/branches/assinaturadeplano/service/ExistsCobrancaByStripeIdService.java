package com.branches.assinaturadeplano.service;

import com.branches.assinaturadeplano.repository.CobrancaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExistsCobrancaByStripeIdService {

    private final CobrancaRepository cobrancaRepository;

    public boolean execute(String stripeInvoiceId) {
        return cobrancaRepository.existsByStripeInvoiceId(stripeInvoiceId);
    }
}
