package com.branches.condicaoclimatica.controller;

import com.branches.condicaoclimatica.dto.request.UpdateCondicaoClimaticaDeRelatorioRequest;
import com.branches.condicaoclimatica.service.UpdateCondicaoClimaticaDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
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
public class UpdateCondicaoClimaticaDeRelatorioController {
    private final UpdateCondicaoClimaticaDeRelatorioService updateCondicaoClimaticaDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/condicao-climatica")
    public ResponseEntity<Void> execute(
            @RequestBody @Valid UpdateCondicaoClimaticaDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId) {

        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateCondicaoClimaticaDeRelatorioService.execute(request, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
