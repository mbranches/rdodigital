package com.branches.configuradores.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.configuradores.dto.response.ModeloDeRelatorioResponse;
import com.branches.configuradores.service.ListAllModelosDeRelatorioService;
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
@Tag(name = "Configuradores")
public class ListAllModelosDeRelatorioController {
    private final ListAllModelosDeRelatorioService listAllModelosDeRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/configuradores/modelos-de-relatorio")
    @Operation(summary = "List all modelos de relatorio", description = "Lista todos os modelos de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de modelos de relatório recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ModeloDeRelatorioResponse>> execute(@PathVariable String tenantExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<ModeloDeRelatorioResponse> response = listAllModelosDeRelatorioService.execute(tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
