package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.response.UserOfObraResponse;
import com.branches.usertenant.domain.UserTenantEntity;
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
@Tag(name = "Obra")
public class ListAllObraUsersController {
    private final ListAllObraUsersService listAllObraUsersService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/users")
    @Operation(summary = "List all obra users", description = "Lista usuários com acesso a obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios listado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<UserOfObraResponse>> execute(@PathVariable String tenantExternalId,
                                                            @PathVariable String obraExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<UserOfObraResponse> response = listAllObraUsersService.execute(obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
