package com.branches.external.stripe;

import com.branches.exception.InternalServerError;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreateStripeCheckoutSession {

    @Value("${stripe.success-url:http://localhost:3000/checkout/sucesso}")
    private String successUrl;

    @Value("${stripe.cancel-url:http://localhost:3000/checkout/cancelado}")
    private String cancelUrl;

    public CreateStripeCheckoutSessionResponse execute(String stripePriceId) {
        try {
            log.info("Criando sessão de checkout no Stripe para o tenant");

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(stripePriceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            log.info("Sessão de checkout criada com sucesso: {}", session.getId());

            return new CreateStripeCheckoutSessionResponse(session.getId(), session.getUrl());
        } catch (StripeException e) {
            log.error("Erro ao criar sessão de checkout no Stripe: {}", e.getMessage(), e);
            throw new InternalServerError("Erro ao criar sessão de checkout: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao criar sessão de checkout: {}", e.getMessage(), e);
            throw new InternalServerError("Erro inesperado ao criar sessão de checkout");
        }
    }
}


