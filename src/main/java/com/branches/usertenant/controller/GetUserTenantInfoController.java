package com.branches.usertenant.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.dto.response.UserTenantInfoResponse;
import com.branches.usertenant.service.GetUserTenantInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GetUserTenantInfoController {
    private final GetUserTenantInfoService getUserTenantInfoService;

    @GetMapping("/api/tenants/{tenantExternalId}/users/me/info")
    public ResponseEntity<UserTenantInfoResponse> execute(@PathVariable String tenantExternalId) {
        Long userId = UserTenantsContext.getUserId();
        List<Long> tenantIds = UserTenantsContext.getTenantIds();

        UserTenantInfoResponse response = getUserTenantInfoService.     execute(tenantExternalId, userId, tenantIds);

        return ResponseEntity.ok(response);
    }
}
