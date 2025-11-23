package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.maodeobra.dto.request.CreateGrupoMaoDeObraRequest;
import com.branches.maodeobra.dto.response.CreateGrupoMaoDeObraResponse;
import com.branches.maodeobra.service.CreateGrupoMaoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CreateGrupoMaoDeObraController {
    private final CreateGrupoMaoDeObraService createGrupoMaoDeObraService;

    @PostMapping("/api/tenants/{tenantExternalId}/grupos-de-mao-de-obra")
    public ResponseEntity<CreateGrupoMaoDeObraResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateGrupoMaoDeObraRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateGrupoMaoDeObraResponse response = createGrupoMaoDeObraService.execute(tenantExternalId, request, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
