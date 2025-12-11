package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.request.CreateOcorrenciaDeRelatorioRequest;
import com.branches.ocorrencia.dto.response.CreateOcorrenciaDeRelatorioResponse;
import com.branches.ocorrencia.service.CreateOcorrenciaDeRelatorioService;
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
@Tag(name = "Ocorrencia")
public class CreateOcorrenciaDeRelatorioController {
    private final CreateOcorrenciaDeRelatorioService createOcorrenciaDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/ocorrencias")
    @Operation(summary = "Create ocorrencia de relatorio", description = "Cria uma nova ocorrência de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ocorrência criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateOcorrenciaDeRelatorioResponse> execute(
            @RequestBody @Valid CreateOcorrenciaDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateOcorrenciaDeRelatorioResponse response = createOcorrenciaDeRelatorioService.execute(request, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
