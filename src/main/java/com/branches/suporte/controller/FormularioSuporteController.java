package com.branches.suporte.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.suporte.FormularioSuporteService;
import com.branches.suporte.dto.request.FormularioSuporteRequest;
import com.branches.user.domain.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FormularioSuporteController {
    private final FormularioSuporteService formularioSuporteService;

    @PostMapping("/api/tenants/{tenantExternalId}/suporte")
    public ResponseEntity<Void> execute(
            @PathVariable String tenantExternalId,
            @RequestBody @Valid FormularioSuporteRequest request
    ) {
        UserEntity requestingUser = UserTenantsContext.getUser();

        formularioSuporteService.execute(tenantExternalId, requestingUser, request);

        return ResponseEntity.noContent().build();
    }
}
