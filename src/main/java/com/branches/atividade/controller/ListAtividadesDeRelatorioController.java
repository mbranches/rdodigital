package com.branches.atividade.controller;

import com.branches.atividade.dto.response.AtividadeDeRelatorioResponse;
import com.branches.atividade.service.ListAtividadesDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
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
@Tag(name = "Atividade")
public class ListAtividadesDeRelatorioController {
    private final ListAtividadesDeRelatorioService listAtividadesDeRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/atividades")
    @Operation(summary = "List atividades de relatorio", description = "Lista as atividades de um relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de atividades recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<AtividadeDeRelatorioResponse>> execute(
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<AtividadeDeRelatorioResponse> response = listAtividadesDeRelatorioService.execute(relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
