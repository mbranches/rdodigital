package com.branches.atividade.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.atividade.service.UpdateAtividadeDeRelatorioService;
import com.branches.relatorio.dto.request.UpdateAtividadeDeRelatorioRequest;
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
public class UpdateAtividadeDeRelatorioController {
    private final UpdateAtividadeDeRelatorioService updateAtividadeDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/atividades/{id}")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateAtividadeDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateAtividadeDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
