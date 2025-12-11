package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.service.DeleteGrupoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
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
@Tag(name = "Obra")
public class DeleteGrupoDeObraController {
    private final DeleteGrupoDeObraService deleteGrupoDeObraService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/grupos-de-obra/{id}")
    @Operation(summary = "Delete grupo de obra", description = "Deleta um grupo de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Grupo de obra deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Grupo de obra não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable Long id, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteGrupoDeObraService.execute(id, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
