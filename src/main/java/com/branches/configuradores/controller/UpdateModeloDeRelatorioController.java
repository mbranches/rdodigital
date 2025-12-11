package com.branches.configuradores.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.configuradores.dto.request.UpdateModeloDeRelatorioRequest;
import com.branches.configuradores.service.UpdateModeloDeRelatorioService;
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
@Tag(name = "Configuradores")
public class UpdateModeloDeRelatorioController {
    private final UpdateModeloDeRelatorioService updateModeloDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/configuradores/modelos-de-relatorio/{id}")
    @Operation(summary = "Update modelo de relatorio", description = "Atualiza um modelo de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Modelo de relatório atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Modelo de relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateModeloDeRelatorioRequest request,
                                        @PathVariable String tenantExternalId,
                                        @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateModeloDeRelatorioService.execute(request, id, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
