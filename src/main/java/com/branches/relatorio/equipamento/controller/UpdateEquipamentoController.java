package com.branches.relatorio.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.equipamento.dto.request.UpdateEquipamentoRequest;
import com.branches.relatorio.equipamento.service.UpdateEquipamentoService;
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
public class UpdateEquipamentoController {
    private final UpdateEquipamentoService updateEquipamentoService;

    @PutMapping("/api/tenants/{tenantExternalId}/equipamentos/{id}")
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable Long id, @RequestBody @Valid UpdateEquipamentoRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateEquipamentoService.execute(id, request, tenantExternalId, userTenants);

        return ResponseEntity.ok().build();
    }
}
