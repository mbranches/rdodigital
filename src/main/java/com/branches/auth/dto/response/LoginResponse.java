package com.branches.auth.dto.response;

import com.branches.usertenant.dto.response.TenantByUserInfoResponse;

import java.util.List;

public record LoginResponse(String accessToken,
                            String refreshToken,
                            List<TenantByUserInfoResponse> tenants) {
}
