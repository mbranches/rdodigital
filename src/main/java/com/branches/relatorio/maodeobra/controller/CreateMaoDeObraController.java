package com.branches.relatorio.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.maodeobra.dto.request.CreateMaoDeObraRequest;
import com.branches.relatorio.maodeobra.dto.response.CreateMaoDeObraResponse;
import com.branches.relatorio.maodeobra.service.CreateMaoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CreateMaoDeObraController {
    private final CreateMaoDeObraService createMaoDeObraService;

    @PostMapping("/api/tenants/{tenantExternalId}/mao-de-obra")
    public ResponseEntity<CreateMaoDeObraResponse> execute(@RequestBody @Valid CreateMaoDeObraRequest request, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateMaoDeObraResponse response = createMaoDeObraService.execute(request, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
