package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.service.RemoveUserFromObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RemoveUserFromObraController {
    private final RemoveUserFromObraService removeUserFromObraService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/users/{userId}")
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId,
                                        @PathVariable String obraExternalId,
                                        @PathVariable String userId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        removeUserFromObraService.execute(userId, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}

