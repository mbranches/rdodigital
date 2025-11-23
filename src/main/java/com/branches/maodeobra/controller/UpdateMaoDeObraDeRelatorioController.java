package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.request.UpdateMaoDeObraDeRelatorioRequest;
import com.branches.maodeobra.service.UpdateMaoDeObraDeRelatorioService;
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
public class UpdateMaoDeObraDeRelatorioController {
    private final UpdateMaoDeObraDeRelatorioService updateMaoDeObraDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/maodeobra/{id}")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateMaoDeObraDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateMaoDeObraDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
