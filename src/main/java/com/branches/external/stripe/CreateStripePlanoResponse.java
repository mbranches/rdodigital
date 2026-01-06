package com.branches.external.stripe;

public record CreateStripePlanoResponse(
        String productId,
        String priceId
) {
}
