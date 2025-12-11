package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.service.DeleteConfiguracaoDeAssinaturaDeRelatorioDeObraService;
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
@Tag(name = "Obra")
public class DeleteConfiguracaoDeAssinaturaDeRelatorioDeObraController {
    private final DeleteConfiguracaoDeAssinaturaDeRelatorioDeObraService deleteConfiguracaoDeAssinaturaDeRelatorioDeObraService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios/assinaturas/{id}")
    @Operation(summary = "Delete configuracao de assinatura de relatorio de obra", description = "Deleta uma configuração de assinatura de relatório de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuração de assinatura deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Configuração não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String obraExternalId, @PathVariable String tenantExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteConfiguracaoDeAssinaturaDeRelatorioDeObraService.execute(id, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
