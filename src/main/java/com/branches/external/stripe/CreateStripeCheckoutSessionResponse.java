package com.branches.external.stripe;

public record CreateStripeCheckoutSessionResponse(
        String sessionId,
        String checkoutUrl
) {
}
