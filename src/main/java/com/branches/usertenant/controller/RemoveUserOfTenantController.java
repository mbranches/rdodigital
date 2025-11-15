package com.branches.usertenant.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.RemoveUserOfTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RemoveUserOfTenantController {
    private final RemoveUserOfTenantService removeUserOfTenantService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/users/{userExternalId}")
    public void execute(@PathVariable String tenantExternalId, @PathVariable String userExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        removeUserOfTenantService.execute(tenantExternalId, userExternalId, userTenants);
    }
}
