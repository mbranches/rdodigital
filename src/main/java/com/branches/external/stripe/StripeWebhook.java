package com.branches.external.stripe;

import com.branches.exception.InternalServerError;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentIntent;
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

    @PostMapping("/webhook/stripe")
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

                // Subscription recorrente normal
                Subscription subscription;
                try {
                    subscription = subscriptionId != null ? Subscription.retrieve(subscriptionId) : null;
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
                Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da invoice do Stripe"));

                String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

                if (subscriptionId != null) {
                    Subscription subscription;
                    try {
                        subscription = Subscription.retrieve(subscriptionId);
                    } catch (StripeException e) {
                        log.error("Erro ao recuperar assinatura do Stripe: {}", e.getMessage());
                        throw new InternalServerError("Erro ao recuperar assinatura do Stripe");
                    }

                    stripeSubscriptionService.handlePaymentFailed(subscription);
                }
            }
            case "invoice.paid" -> {
                Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da invoice do Stripe"));

                String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

                if (subscriptionId != null) {
                    Subscription subscription;
                    try {
                        subscription = Subscription.retrieve(subscriptionId);
                    } catch (StripeException e) {
                        log.error("Erro ao recuperar assinatura do Stripe: {}", e.getMessage());
                        throw new InternalServerError("Erro ao recuperar assinatura do Stripe");
                    }

                    stripeSubscriptionService.handlePaymentSucceeded(subscription);
                }
            }
            case "payment_intent.succeeded" -> {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto do PaymentIntent do Stripe"));

                log.info("Pagamento confirmado via PaymentIntent: {}", paymentIntent.getId());

                // Verifica se há invoice associada
                if (paymentIntent.getMetadata() != null && paymentIntent.getMetadata().containsKey("invoice_id")) {
                    String invoiceId = paymentIntent.getMetadata().get("invoice_id");

                    try {
                        Invoice invoice = Invoice.retrieve(invoiceId);
                        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

                        if (subscriptionId != null) {
                            Subscription subscription = Subscription.retrieve(subscriptionId);
                            stripeSubscriptionService.handlePaymentSucceeded(subscription);
                            log.info("Assinatura ativada após pagamento confirmado: {}", subscriptionId);
                        }
                    } catch (StripeException e) {
                        log.error("Erro ao processar pagamento: {}", e.getMessage());
                        throw new InternalServerError("Erro ao processar pagamento");
                    }
                }
            }
            case "checkout.session.async_payment_succeeded" -> {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da sessão do Stripe"));

                log.info("Pagamento assíncrono bem-sucedido para sessão: {}", session.getId());

                String subscriptionId = session.getSubscription();

                if (subscriptionId != null) {
                    Subscription subscription;
                    try {
                        subscription = Subscription.retrieve(subscriptionId);
                        stripeSubscriptionService.handlePaymentSucceeded(subscription);
                        log.info("Assinatura ativada após pagamento assíncrono bem-sucedido: {}", subscriptionId);
                    } catch (StripeException e) {
                        log.error("Erro ao recuperar assinatura do Stripe: {}", e.getMessage());
                        throw new InternalServerError("Erro ao recuperar assinatura do Stripe");
                    }
                }
            }
            case "checkout.session.async_payment_failed" -> {
                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject().orElseThrow(() -> new InternalServerError("Erro ao desserializar objeto da sessão do Stripe"));

                log.info("Pagamento assíncrono falhou para sessão: {}", session.getId());

                String subscriptionId = session.getSubscription();

                if (subscriptionId != null) {
                    Subscription subscription;
                    try {
                        subscription = Subscription.retrieve(subscriptionId);
                        stripeSubscriptionService.handlePaymentFailed(subscription);
                        log.info("Assinatura marcada como falha após pagamento assíncrono falhar: {}", subscriptionId);
                    } catch (StripeException e) {
                        log.error("Erro ao recuperar assinatura do Stripe: {}", e.getMessage());
                        throw new InternalServerError("Erro ao recuperar assinatura do Stripe");
                    }
                }
            }

    }

        log.info("webhook do Stripe processado com sucesso");

        return ResponseEntity.ok().build();
    }
}