package com.branches.usertenant.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.dto.response.UserTenantResponse;
import com.branches.usertenant.service.ListUserTenantsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "UserTenant")
public class ListUserTenantsController {
    private final ListUserTenantsService listUserTenantsService;

    @GetMapping("/api/tenants/{tenantExternalId}/users")
    @Operation(summary = "List user tenants", description = "Lista todos os usuários de um tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Tenant não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<UserTenantResponse>> execute(
            @PathVariable String tenantExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<UserTenantResponse> response = listUserTenantsService.execute(tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
