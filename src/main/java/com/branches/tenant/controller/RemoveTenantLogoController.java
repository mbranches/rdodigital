package com.branches.tenant.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.tenant.service.RemoveTenantLogoService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Tenant")
public class RemoveTenantLogoController {
    private final RemoveTenantLogoService removeTenantLogoService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/logo")
    @Operation(summary = "Remove tenant logo", description = "Remove o logo de um tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logo do tenant removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Tenant não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenantEntities = UserTenantsContext.getUserTenants();

        removeTenantLogoService.execute(tenantExternalId, userTenantEntities);

        return ResponseEntity.noContent().build();

    }
}
