package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.response.OcorrenciaDeRelatorioResponse;
import com.branches.ocorrencia.service.ListOcorrenciasDeRelatorioService;
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
@Tag(name = "Ocorrencia")
public class ListOcorrenciasDeRelatorioController {
    private final ListOcorrenciasDeRelatorioService listOcorrenciasDeRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/ocorrencias")
    @Operation(summary = "List ocorrencias de relatorio", description = "Lista as ocorrências de um relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ocorrências recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<OcorrenciaDeRelatorioResponse>> execute(
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<OcorrenciaDeRelatorioResponse> response = listOcorrenciasDeRelatorioService.execute(relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
