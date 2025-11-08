package com.branches.relatorio.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.equipamento.service.DeleteEquipamentoService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DeleteEquipamentoController {
    private final DeleteEquipamentoService deleteEquipamentoService;

    @DeleteMapping("/api/tenants/{externalTenantId}/equipamentos/{id}")
    public ResponseEntity<Void> execute(@PathVariable Long id, @PathVariable String externalTenantId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteEquipamentoService.execute(id, externalTenantId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
