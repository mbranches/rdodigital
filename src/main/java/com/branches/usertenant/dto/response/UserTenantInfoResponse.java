package com.branches.usertenant.dto.response;

import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.projection.TenantInfoProjection;
import com.branches.user.repository.projection.UserInfoProjection;

import java.util.List;

public record UserTenantInfoResponse(
        
        UserInfoResponse user,
        TenantInfoResponse tenant
) {
    public static UserTenantInfoResponse from(UserInfoProjection user, List<TenantEntity> allUserTenants, TenantInfoProjection tenant) {
        UserInfoResponse userInfoResponse = UserInfoResponse.from(user, allUserTenants);
        TenantInfoResponse tenantInfoResponse = TenantInfoResponse.from(tenant);

        return new UserTenantInfoResponse(userInfoResponse, tenantInfoResponse);
    }
}
