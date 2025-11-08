package com.branches.auth.dto.response;

import com.branches.usertenant.dto.response.TenantByUserInfoResponse;

import java.util.List;

public record LoginResponse(String accessToken, List<TenantByUserInfoResponse> tenants) {
}
