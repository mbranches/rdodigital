package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.UpdateConfiguracoesDeRelatoriosDeObraRequest;
import com.branches.obra.service.UpdateConfiguracoesDeRelatoriosDeObraService;
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
public class UpdateConfiguracoesDeRelatoriosDeObraController {
    private final UpdateConfiguracoesDeRelatoriosDeObraService updateConfiguracoesDeRelatoriosDeObraService;

    @PutMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateConfiguracoesDeRelatoriosDeObraRequest request, @PathVariable String obraExternalId, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateConfiguracoesDeRelatoriosDeObraService.execute(request, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
