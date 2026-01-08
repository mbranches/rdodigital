package com.branches.plano.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.plano.service.IniciarPeriodoTesteService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Plano")
public class IniciarPeriodoTesteController {
    private final IniciarPeriodoTesteService iniciarPeriodoTesteService;

    @PostMapping("/api/tenants/{tenantExternalId}/periodo-teste/iniciar")
    @Operation(summary = "Iniciar período de teste", description = "Inicia o período de teste para um tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Período de teste iniciado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Tenant não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        iniciarPeriodoTesteService.execute(tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
