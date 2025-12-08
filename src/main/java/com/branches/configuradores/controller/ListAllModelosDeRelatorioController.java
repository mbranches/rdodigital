package com.branches.configuradores.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.configuradores.dto.response.ModeloDeRelatorioResponse;
import com.branches.configuradores.service.ListAllModelosDeRelatorioService;
import com.branches.usertenant.domain.UserTenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ListAllModelosDeRelatorioController {
    private final ListAllModelosDeRelatorioService listAllModelosDeRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/configuradores/modelos-de-relatorio")
    public ResponseEntity<List<ModeloDeRelatorioResponse>> execute(@PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<ModeloDeRelatorioResponse> response = listAllModelosDeRelatorioService.execute(tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
