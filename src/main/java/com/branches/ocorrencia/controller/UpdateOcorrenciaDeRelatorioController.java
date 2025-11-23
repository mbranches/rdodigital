package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.request.UpdateOcorrenciaDeRelatorioRequest;
import com.branches.ocorrencia.service.UpdateOcorrenciaDeRelatorioService;
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
public class UpdateOcorrenciaDeRelatorioController {
    private final UpdateOcorrenciaDeRelatorioService updateOcorrenciaDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/ocorrencias/{id}")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateOcorrenciaDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateOcorrenciaDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
