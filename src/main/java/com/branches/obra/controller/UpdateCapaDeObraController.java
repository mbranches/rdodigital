package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.UpdateCapaDeObraRequest;
import com.branches.obra.service.UpdateCapaDeObraService;
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
public class UpdateCapaDeObraController {
    private final UpdateCapaDeObraService updateCapaDeObraService;

    @PutMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/capa")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateCapaDeObraRequest request,
                                        @PathVariable String tenantExternalId,
                                        @PathVariable String obraExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateCapaDeObraService.execute(request, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
