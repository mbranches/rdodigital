package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.request.CreateTipoDeOcorrenciaRequest;
import com.branches.ocorrencia.dto.response.CreateTipoDeOcorrenciaResponse;
import com.branches.ocorrencia.service.CreateTipoDeOcorrenciaService;
import com.branches.usertenant.domain.UserTenantEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CreateTipoDeOcorrenciaController {
    private final CreateTipoDeOcorrenciaService createTipoDeOcorrenciaService;

    @PostMapping("/api/tenants/{tenantExternalId}/tipos-de-ocorrencia")
    public ResponseEntity<CreateTipoDeOcorrenciaResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateTipoDeOcorrenciaRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateTipoDeOcorrenciaResponse response = createTipoDeOcorrenciaService.execute(tenantExternalId, request, userTenants);

        return ResponseEntity.ok().body(response);
    }
}
