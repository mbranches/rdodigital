package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.maodeobra.dto.response.GrupoMaoDeObraResponse;
import com.branches.maodeobra.service.ListAllGruposMaoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ListAllGrupoMaoDeObraController {
    private final ListAllGruposMaoDeObraService listAllGruposMaoDeObraService;

    @GetMapping("/api/tenants/{tenantExternalId}/grupos-de-mao-de-obra")
    public ResponseEntity<List<GrupoMaoDeObraResponse>> execute(@PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<GrupoMaoDeObraResponse> response = listAllGruposMaoDeObraService.execute(tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}

