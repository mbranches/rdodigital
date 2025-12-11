package com.branches.maodeobra.controller;

import com.branches.maodeobra.service.DeleteMaoDeObraDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
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
@Tag(name = "MaoDeObra")
public class DeleteMaoDeObraDeRelatorioController {
    private final DeleteMaoDeObraDeRelatorioService deleteMaoDeObraDeRelatorioService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/maodeobra/{id}")
    @Operation(summary = "Delete mao de obra de relatorio", description = "Deleta uma mão de obra de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mão de obra de relatório deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Mão de obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteMaoDeObraDeRelatorioService.execute(id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
