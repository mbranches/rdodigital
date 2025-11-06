package com.branches.config.security;

import com.branches.user.domain.UserTenantEntity;

import java.util.List;

public class UserTenantsContext {

    private static ThreadLocal<List<UserTenantEntity>> userTenants = new ThreadLocal<>();

    public static void setUserTenants(List<UserTenantEntity> tenantIds) {
        userTenants.set(tenantIds);
    }

    public static List<UserTenantEntity> getUserTenants() {
        return userTenants.get();
    }

    public static void cleanup() {
        userTenants.remove();
    }
}
