package com.branches.external.stripe;

import com.branches.exception.InternalServerError;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StripeWebhook {
    private final StripeSubscriptionService stripeSubscriptionService;
    @Value("${stripe.secret-webhook}")
    private String secretWebhook;

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> execute(@RequestBody String payload,
                                         @RequestHeader("Stripe-Signature") String signature) {

        log.info("Recebido webhook do Stripe: {}", payload);
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, secretWebhook);
        } catch (Exception e) {
            log.error("Erro ao validar webhook do Stripe: {}", e.getMessage());

            return ResponseEntity.badRequest().build();
        }

        log.info("Processando evento do Stripe: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da sessão do Stripe"));

                String subscriptionId = session.getSubscription();
                Subscription subscription;
                try {
                    subscription = Subscription.retrieve(subscriptionId);
                } catch (StripeException e) {
                    log.error("Erro ao recuperar assinatura do Stripe: {}", e.getMessage());

                    throw new InternalServerError("Erro ao recuperar assinatura do Stripe");
                }

                stripeSubscriptionService.register(session.getId(), subscription);
            }

            case "customer.subscription.deleted" -> {
                Subscription sub = (Subscription) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da assinatura do Stripe"));

                stripeSubscriptionService.cancel(sub.getId());
            }

            case "customer.subscription.updated" -> {
                Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da assinatura do Stripe"));

                stripeSubscriptionService.update(subscription);
            }
            case "invoice.payment_failed" -> {
                Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da assinatura do Stripe"));

                stripeSubscriptionService.handlePaymentFailed(subscription);
            }
            case "invoice.paid" -> {
                Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da assinatura do Stripe"));

                stripeSubscriptionService.handlePaymentSucceeded(subscription);
            }
        }

        log.info("webhook do Stripe processado com sucesso");

        return ResponseEntity.ok().build();
    }
}