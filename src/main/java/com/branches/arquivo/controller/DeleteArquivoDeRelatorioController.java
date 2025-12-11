package com.branches.arquivo.controller;

import com.branches.arquivo.service.DeleteArquivoDeRelatorioService;
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
@Tag(name = "Arquivo")
public class DeleteArquivoDeRelatorioController {
    private final DeleteArquivoDeRelatorioService deleteArquivoDeRelatorioService;

    @DeleteMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/arquivos/{arquivoId}")
    @Operation(summary = "Delete arquivo de relatorio", description = "Deleta um arquivo de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Arquivo deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId,
                                        @PathVariable String relatorioExternalId,
                                        @PathVariable Long arquivoId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteArquivoDeRelatorioService.execute(arquivoId, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
