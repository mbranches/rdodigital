package com.branches.external.stripe;

import com.branches.exception.InternalServerError;
import com.branches.plano.domain.enums.RecorrenciaPlano;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CreateStripeCheckoutSession {

    @Value("${stripe.success-url:http://localhost:3000/checkout/sucesso}")
    private String successUrl;

    @Value("${stripe.cancel-url:http://localhost:3000/checkout/cancelado}")
    private String cancelUrl;

    public CreateStripeCheckoutSessionResponse execute(String stripePriceId, RecorrenciaPlano recorrencia) {
        try {
            log.info("Criando sessão de checkout no Stripe para o tenant com recorrência: {}", recorrencia);

            SessionCreateParams.Mode mode = recorrencia == RecorrenciaPlano.MENSAL_AVULSO
                    ? SessionCreateParams.Mode.PAYMENT
                    : SessionCreateParams.Mode.SUBSCRIPTION;

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(mode)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addAllPaymentMethodType(
                            List.of(SessionCreateParams.PaymentMethodType.CARD,
                                    SessionCreateParams.PaymentMethodType.BOLETO)
                    )
                    .setPaymentMethodOptions(
                            SessionCreateParams.PaymentMethodOptions.builder()
                                    .setBoleto(
                                            SessionCreateParams.PaymentMethodOptions.Boleto.builder()
                                                    .setExpiresAfterDays(3L)
                                                    .build()
                                    )
                                    .build()
                    )
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setPrice(stripePriceId)
                                    .setQuantity(1L)
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            log.info("Sessão de checkout criada com sucesso: {} no modo: {}", session.getId(), mode);

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


