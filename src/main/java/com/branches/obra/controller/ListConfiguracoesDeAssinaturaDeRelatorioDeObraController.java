package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.response.ConfiguracaoDeAssinaturaDeRelatorioResponse;
import com.branches.obra.service.ListConfiguracoesDeAssinaturaDeRelatorioDeObraService;
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
public class ListConfiguracoesDeAssinaturaDeRelatorioDeObraController {
    private final ListConfiguracoesDeAssinaturaDeRelatorioDeObraService listConfiguracoesDeAssinaturaDeRelatorioDeObraService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios/assinaturas")
    @Operation(summary = "List configuracoes de assinatura de relatorio de obra", description = "Lista as configurações de assinatura de relatório de uma obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de configurações recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ConfiguracaoDeAssinaturaDeRelatorioResponse>> execute(@PathVariable String obraExternalId, @PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<ConfiguracaoDeAssinaturaDeRelatorioResponse> response = listConfiguracoesDeAssinaturaDeRelatorioDeObraService.execute(obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
