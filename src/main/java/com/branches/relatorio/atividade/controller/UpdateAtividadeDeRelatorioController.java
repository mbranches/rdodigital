package com.branches.relatorio.atividade.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.atividade.service.UpdateAtividadesDeRelatorioService;
import com.branches.relatorio.rdo.dto.request.UpdateAtividadeDeRelatorioRequest;
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
    private final UpdateAtividadesDeRelatorioService updateAtividadesDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/atividades/{id}")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateAtividadeDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateAtividadesDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
