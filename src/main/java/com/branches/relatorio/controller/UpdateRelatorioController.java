package com.branches.relatorio.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.request.UpdateRelatorioRequest;
import com.branches.relatorio.service.UpdateRelatorioService;
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
public class UpdateRelatorioController {
    private final UpdateRelatorioService updateRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}")
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @RequestBody @Valid UpdateRelatorioRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateRelatorioService.execute(request, tenantExternalId, relatorioExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
