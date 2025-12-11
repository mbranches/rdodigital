package com.branches.relatorio.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.response.GetRelatorioDetailsResponse;
import com.branches.relatorio.service.GetRelatorioDetailsService;
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
@Tag(name = "Relatorio")
public class GetRelatorioDetailsController {
    private final GetRelatorioDetailsService getRelatorioDetailsService;

    @GetMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}")
    @Operation(summary = "Get relatorio details", description = "Obtém os detalhes de um relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes do relatório recuperados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<GetRelatorioDetailsResponse> execute(@PathVariable String tenantExternalId,
                                                               @PathVariable String relatorioExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        GetRelatorioDetailsResponse response = getRelatorioDetailsService.execute(
                tenantExternalId,
                relatorioExternalId,
                userTenants
        );

        return ResponseEntity.ok(response);
    }
}
