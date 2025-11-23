package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.response.TipoDeOcorrenciaResponse;
import com.branches.ocorrencia.service.ListAllTiposDeOcorrenciaService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ListAllTiposDeOcorrenciaController {
    private final ListAllTiposDeOcorrenciaService listAllTiposDeOcorrenciaService;

    @GetMapping("/api/tenants/{externalTenantId}/tipoDeOcorrencias")
    public ResponseEntity<List<TipoDeOcorrenciaResponse>> execute(@PathVariable String externalTenantId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<TipoDeOcorrenciaResponse> response = listAllTiposDeOcorrenciaService.execute(externalTenantId, userTenants);

        return ResponseEntity.ok(response);
    }
}
