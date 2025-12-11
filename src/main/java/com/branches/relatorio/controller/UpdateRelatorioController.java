package com.branches.relatorio.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.request.UpdateRelatorioRequest;
import com.branches.relatorio.service.UpdateRelatorioService;
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
@Tag(name = "Relatorio")
public class UpdateRelatorioController {
    private final UpdateRelatorioService updateRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}")
    @Operation(summary = "Update relatorio", description = "Atualiza um relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relatório atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @RequestBody @Valid UpdateRelatorioRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateRelatorioService.execute(request, tenantExternalId, relatorioExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
