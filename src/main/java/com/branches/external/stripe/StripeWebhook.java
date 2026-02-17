package com.branches.external.stripe;

import com.branches.exception.BadRequestException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
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
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;
    private final StripeEventsHandlerService hubStripeEventsHandlerService;

    @PostMapping("/webhook/stripe")
    public ResponseEntity<Void> execute(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) {
        log.info("Recebido webhook do Stripe: {}", payload);

        Event event;

        try {
            event = Webhook.constructEvent(
                    payload,
                    signature,
                    webhookSecret
            );
        } catch (SignatureVerificationException e) {
            log.error("Assinatura inválida do webhook Stripe");

            throw new BadRequestException("Assinatura inválida do webhook Stripe");
        }

        hubStripeEventsHandlerService.handle(event);

        return ResponseEntity.ok().build();
    }
}
