package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.request.UpdateTipoDeOcorrenciaRequest;
import com.branches.ocorrencia.service.UpdateTipoDeOcorrenciaService;
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
public class UpdateTipoDeOcorrenciaController {
    private final UpdateTipoDeOcorrenciaService updateTipoDeOcorrenciaService;

    @PutMapping("/api/tenants/{tenantExternalId}/tipos-de-ocorrencia/{id}")
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable Long id, @RequestBody @Valid UpdateTipoDeOcorrenciaRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateTipoDeOcorrenciaService.execute(id, request, tenantExternalId, userTenants);

        return ResponseEntity.ok().build();
    }
}
