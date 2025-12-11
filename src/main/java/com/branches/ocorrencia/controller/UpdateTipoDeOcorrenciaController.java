package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.request.UpdateTipoDeOcorrenciaRequest;
import com.branches.ocorrencia.service.UpdateTipoDeOcorrenciaService;
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
@Tag(name = "Ocorrencia")
public class UpdateTipoDeOcorrenciaController {
    private final UpdateTipoDeOcorrenciaService updateTipoDeOcorrenciaService;

    @PutMapping("/api/tenants/{tenantExternalId}/tipos-de-ocorrencia/{id}")
    @Operation(summary = "Update tipo de ocorrencia", description = "Atualiza um tipo de ocorrência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de ocorrência atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Tipo de ocorrência não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable Long id, @RequestBody @Valid UpdateTipoDeOcorrenciaRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateTipoDeOcorrenciaService.execute(id, request, tenantExternalId, userTenants);

        return ResponseEntity.ok().build();
    }
}
