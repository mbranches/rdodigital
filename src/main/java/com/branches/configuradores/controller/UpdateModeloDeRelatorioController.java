package com.branches.configuradores.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.configuradores.dto.request.UpdateModeloDeRelatorioRequest;
import com.branches.configuradores.service.UpdateModeloDeRelatorioService;
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
public class UpdateModeloDeRelatorioController {
    private final UpdateModeloDeRelatorioService updateModeloDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/configuradores/modelos-de-relatorio/{id}")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateModeloDeRelatorioRequest request,
                                        @PathVariable String tenantExternalId,
                                        @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateModeloDeRelatorioService.execute(request, id, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
