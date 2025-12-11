package com.branches.maodeobra.controller;

import com.branches.maodeobra.dto.request.CreateMaoDeObraDeRelatorioRequest;
import com.branches.maodeobra.dto.response.CreateMaoDeObraDeRelatorioResponse;
import com.branches.maodeobra.service.CreateMaoDeObraDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "MaoDeObra")
public class CreateMaoDeObraDeRelatorioController {
    private final CreateMaoDeObraDeRelatorioService createMaoDeObraDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/maodeobra")
    @Operation(summary = "Create mao de obra de relatorio", description = "Cria uma nova mão de obra de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mão de obra de relatório criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateMaoDeObraDeRelatorioResponse> execute(
            @RequestBody @Valid CreateMaoDeObraDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId) {

        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateMaoDeObraDeRelatorioResponse response = createMaoDeObraDeRelatorioService.execute(
                request,
                relatorioExternalId,
                tenantExternalId,
                userTenants
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
