package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.response.AnaliseDeMateriaisPorMesResponse;
import com.branches.material.service.GetAnaliseMateriaisPorMesService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Material")
public class GetAnaliseMateriaisPorMesController {
    private final GetAnaliseMateriaisPorMesService getAnaliseMateriaisPorMesService;

    @Operation(summary = "Get quantidade de material por mês durante um ano", description = "Obtém a quantidade de materiais utilizados por mês durante um ano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantidade de materiais por mês obtida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/api/tenants/{tenantExternalId}/materiais/analisys/quantidade-por-mes")
    public ResponseEntity<AnaliseDeMateriaisPorMesResponse> execute(
            @PathVariable String tenantExternalId,
            @RequestParam Integer year,
            @RequestParam(required = false) String obraExternalId,
            @RequestParam(required = false) Long materialIdPraFiltrarTotalPorMes
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        AnaliseDeMateriaisPorMesResponse response = getAnaliseMateriaisPorMesService.execute(
                tenantExternalId,
                year,
                obraExternalId,
                materialIdPraFiltrarTotalPorMes,
                userTenants
        );

        return ResponseEntity.ok(response);
    }
}
