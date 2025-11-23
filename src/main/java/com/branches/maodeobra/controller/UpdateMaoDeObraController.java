package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.maodeobra.dto.request.UpdateMaoDeObraRequest;
import com.branches.maodeobra.service.UpdateMaoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UpdateMaoDeObraController {
    private final UpdateMaoDeObraService updateMaoDeObraService;

    @PutMapping("/api/tenants/{tenantExternalId}/mao-de-obra/{id}")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateMaoDeObraRequest request, @PathVariable Long id, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateMaoDeObraService.execute(request, id, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
