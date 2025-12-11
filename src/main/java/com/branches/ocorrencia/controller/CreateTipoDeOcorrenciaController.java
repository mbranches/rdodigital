package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.request.CreateTipoDeOcorrenciaRequest;
import com.branches.ocorrencia.dto.response.CreateTipoDeOcorrenciaResponse;
import com.branches.ocorrencia.service.CreateTipoDeOcorrenciaService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Ocorrencia")
public class CreateTipoDeOcorrenciaController {
    private final CreateTipoDeOcorrenciaService createTipoDeOcorrenciaService;

    @PostMapping("/api/tenants/{tenantExternalId}/tipos-de-ocorrencia")
    @Operation(summary = "Create tipo de ocorrencia", description = "Cria um novo tipo de ocorrência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de ocorrência criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateTipoDeOcorrenciaResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateTipoDeOcorrenciaRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateTipoDeOcorrenciaResponse response = createTipoDeOcorrenciaService.execute(tenantExternalId, request, userTenants);

        return ResponseEntity.ok().body(response);
    }
}
