package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.service.DeleteCapaDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeleteCapaDeObraController {
    private final DeleteCapaDeObraService deleteCapaDeObraService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/capa")
    @Operation(summary = "Delete capa de obra", description = "Deleta a capa de uma obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Capa removida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId,
                                        @PathVariable String obraExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteCapaDeObraService.execute(obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
