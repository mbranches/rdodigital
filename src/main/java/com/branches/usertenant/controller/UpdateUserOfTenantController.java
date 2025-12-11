package com.branches.usertenant.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.dto.request.UpdateUserOfTenantRequest;
import com.branches.usertenant.service.UpdateUserOfTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "UserTenant")
public class UpdateUserOfTenantController {
    private final UpdateUserOfTenantService updateUserOfTenantService;

    @PutMapping("/api/tenants/{tenantExternalId}/users/{userExternalId}")
    @Operation(summary = "Update user of tenant", description = "Atualiza as informações de um usuário de um tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Informações do usuário atualizadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou tenant não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateUserOfTenantRequest request, @PathVariable String tenantExternalId, @PathVariable String userExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateUserOfTenantService.execute(request, tenantExternalId, userExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
