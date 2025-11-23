package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.maodeobra.dto.request.UpdateGrupoMaoDeObraRequest;
import com.branches.maodeobra.service.UpdateGrupoMaoDeObraService;
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
public class UpdateGrupoMaoDeObraController {
    private final UpdateGrupoMaoDeObraService updateGrupoMaoDeObraService;

    @PutMapping("/api/tenants/{tenantExternalId}/grupos-de-mao-de-obra/{id}")
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable Long id, @RequestBody @Valid UpdateGrupoMaoDeObraRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateGrupoMaoDeObraService.execute(id, tenantExternalId, request, userTenants);

        return ResponseEntity.noContent().build();
    }
}

