package com.branches.usertenant.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.RemoveUserOfTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "UserTenant")
public class RemoveUserOfTenantController {
    private final RemoveUserOfTenantService removeUserOfTenantService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/users/{userExternalId}")
    @Operation(summary = "Remove user of tenant", description = "Remove um usuário de um tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário removido do tenant com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou tenant não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable String userExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        removeUserOfTenantService.execute(tenantExternalId, userExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
