package com.branches.condicaoclimatica.controller;

import com.branches.condicaoclimatica.dto.request.UpdateCondicaoClimaticaDeRelatorioRequest;
import com.branches.condicaoclimatica.service.UpdateCondicaoClimaticaDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "CondicaoClimatica")
public class UpdateCondicaoClimaticaDeRelatorioController {
    private final UpdateCondicaoClimaticaDeRelatorioService updateCondicaoClimaticaDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/condicao-climatica")
    @Operation(summary = "Update condicao climatica de relatorio", description = "Atualiza a condição climática de um relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Condição climática atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(
            @RequestBody @Valid UpdateCondicaoClimaticaDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId) {

        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateCondicaoClimaticaDeRelatorioService.execute(request, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
