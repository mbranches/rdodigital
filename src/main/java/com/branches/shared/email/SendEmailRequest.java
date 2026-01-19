package com.branches.shared.email;

import lombok.Builder;

@Builder
public record SendEmailRequest(
        String to,
        String subject,
        String body
) {}
