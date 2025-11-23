package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.service.DeleteTipoDeOcorrenciaService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DeleteTipoDeOcorrenciaController {
    private final DeleteTipoDeOcorrenciaService deleteTipoDeOcorrenciaService;

    @DeleteMapping("/api/tenants/{externalTenantId}/tipos-de-ocorrencia/{id}")
    public ResponseEntity<Void> execute(@PathVariable Long id, @PathVariable String externalTenantId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteTipoDeOcorrenciaService.execute(id, externalTenantId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
