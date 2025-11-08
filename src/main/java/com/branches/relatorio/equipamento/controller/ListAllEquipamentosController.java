package com.branches.relatorio.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.equipamento.dto.response.EquipamentoResponse;
import com.branches.relatorio.equipamento.service.ListAllEquipamentosService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ListAllEquipamentosController {
    private final ListAllEquipamentosService listAllEquipamentosService;

    @GetMapping("/api/tenants/{externalTenantId}/equipamentos")
    public ResponseEntity<List<EquipamentoResponse>> execute(@PathVariable String externalTenantId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<EquipamentoResponse> response = listAllEquipamentosService.execute(externalTenantId, userTenants);

        return ResponseEntity.ok(response);
    }
}
