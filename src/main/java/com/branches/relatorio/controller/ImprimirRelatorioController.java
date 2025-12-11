package com.branches.relatorio.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.response.ImprimirRelatorioResponse;
import com.branches.relatorio.service.ImprimirRelatorioService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Relatorio")
public class ImprimirRelatorioController {
    private final ImprimirRelatorioService imprimirRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/pdf/imprimir")
    @Operation(summary = "Imprimir relatorio", description = "Gera o PDF de um relatório para impressão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ImprimirRelatorioResponse> execute(@PathVariable String tenantExternalId,
                                                             @PathVariable String relatorioExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        ImprimirRelatorioResponse response = imprimirRelatorioService.execute(relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
