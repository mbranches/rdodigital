package com.branches.configuradores.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.configuradores.dto.request.CreateModeloDeRelatorioRequest;
import com.branches.configuradores.dto.response.ModeloDeRelatorioResponse;
import com.branches.configuradores.service.CreateModeloDeRelatorioService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Configuradores")
public class CreateModeloDeRelatorioController {
    private final CreateModeloDeRelatorioService createModeloDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/configuradores/modelos-de-relatorio")
    @Operation(summary = "Create modelo de relatorio", description = "Cria um novo modelo de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Modelo de relatório criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ModeloDeRelatorioResponse> execute(@PathVariable String tenantExternalId,
                                                             @RequestBody CreateModeloDeRelatorioRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        ModeloDeRelatorioResponse response = createModeloDeRelatorioService.execute(request, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
