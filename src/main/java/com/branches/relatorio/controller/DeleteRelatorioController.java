package com.branches.relatorio.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.service.DeleteRelatorioService;
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
@Tag(name = "Relatorio")
public class DeleteRelatorioController {
    private final DeleteRelatorioService deleteRelatorioService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}")
    @Operation(summary = "Delete relatorio", description = "Deleta um relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relatório deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String relatorioExternalId,
                                        @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteRelatorioService.execute(relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
