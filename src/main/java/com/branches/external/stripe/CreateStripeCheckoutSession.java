package com.branches.external.stripe;

import com.branches.exception.InternalServerError;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.PaymentMethodOptions;
import com.stripe.param.checkout.SessionCreateParams.PaymentMethodType;
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

    public CreateStripeCheckoutSessionResponse execute(String stripePriceId, String customerId, Long tenantId) {
        try {
            log.info("Criando sessão de checkout no Stripe para o tenant: {}", tenantId);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .addAllPaymentMethodType(
                            List.of(PaymentMethodType.CARD,
                                    PaymentMethodType.BOLETO)
                    )
                    .setPaymentMethodOptions(
                            PaymentMethodOptions.builder()
                                    .setBoleto(
                                            PaymentMethodOptions.Boleto.builder()
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
                    .setCustomer(customerId)
                    .putMetadata("tenantId", tenantId.toString())
                    .build();

            Session session = Session.create(params);
            log.info("Sessão de checkout criada com sucesso: {}", session.getId());

            return new CreateStripeCheckoutSessionResponse(session.getId(), session.getUrl(), session.getSubscription());
        } catch (StripeException e) {
            log.error("Erro ao criar sessão de checkout no Stripe: {}", e.getMessage(), e);
            throw new InternalServerError("Erro ao criar sessão de checkout: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao criar sessão de checkout: {}", e.getMessage(), e);
            throw new InternalServerError("Erro inesperado ao criar sessão de checkout");
        }
    }
}


