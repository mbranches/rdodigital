package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.response.ConfiguracaoRelatoriosResponse;
import com.branches.obra.service.GetConfiguracaoRelatoriosService;
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
public class GetConfiguracoesDeRelatorioDeObraController {
    private final GetConfiguracaoRelatoriosService getConfiguracaoRelatoriosService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/configuracao-relatorios")
    @Operation(summary = "Get configuracoes de relatorio de obra", description = "Obtém as configurações de relatório de uma obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurações recuperadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ConfiguracaoRelatoriosResponse> execute(@PathVariable String tenantExternalId, @PathVariable String obraExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        ConfiguracaoRelatoriosResponse response = getConfiguracaoRelatoriosService.execute(tenantExternalId, obraExternalId, userTenants);

        return ResponseEntity.ok(response);
    }

}
