package com.branches.auth.dto;

public record ClientInfo(
        String addressIp,
        String userAgent,
        String device,
        String browser,
        String os
) {
}
