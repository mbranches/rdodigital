package com.branches.auth.dto.response;

public record RefreshTokenResponse(String accessToken,
                                   String refreshToken) {
}
