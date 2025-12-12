package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.response.TipoDeOcorrenciaResponse;
import com.branches.ocorrencia.service.ListAllTiposDeOcorrenciaService;
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
public class ListAllTiposDeOcorrenciaController {
    private final ListAllTiposDeOcorrenciaService listAllTiposDeOcorrenciaService;

    @GetMapping("/api/tenants/{externalTenantId}/tipos-de-ocorrencia")
    @Operation(summary = "List all tipos de ocorrencia", description = "Lista todos os tipos de ocorrência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de ocorrência recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<TipoDeOcorrenciaResponse>> execute(@PathVariable String externalTenantId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<TipoDeOcorrenciaResponse> response = listAllTiposDeOcorrenciaService.execute(externalTenantId, userTenants);

        return ResponseEntity.ok(response);
    }
}
