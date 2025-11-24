package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.request.UpdateMaterialDeRelatorioRequest;
import com.branches.material.service.UpdateMaterialDeRelatorioService;
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
public class UpdateMaterialDeRelatorioController {
    private final UpdateMaterialDeRelatorioService updateMaterialDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/materiais/{id}")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateMaterialDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateMaterialDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
