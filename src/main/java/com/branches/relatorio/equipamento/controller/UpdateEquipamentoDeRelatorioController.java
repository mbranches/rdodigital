package com.branches.relatorio.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.equipamento.service.UpdateEquipamentosDeRelatorioService;
import com.branches.relatorio.rdo.dto.request.UpdateEquipamentoDeRelatorioRequest;
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
public class UpdateEquipamentoDeRelatorioController {
    private final UpdateEquipamentosDeRelatorioService updateEquipamentosDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/equipamentos/{id}")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateEquipamentoDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateEquipamentosDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
