package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.maodeobra.service.DeleteGrupoMaoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DeleteGrupoMaoDeObraController {
    private final DeleteGrupoMaoDeObraService deleteGrupoMaoDeObraService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/grupos-de-mao-de-obra/{id}")
    public ResponseEntity<Void> execute(@PathVariable Long id, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteGrupoMaoDeObraService.execute(id, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}

