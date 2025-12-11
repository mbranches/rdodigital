package com.branches.ocorrencia.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.service.DeleteTipoDeOcorrenciaService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Ocorrencia")
public class DeleteTipoDeOcorrenciaController {
    private final DeleteTipoDeOcorrenciaService deleteTipoDeOcorrenciaService;

    @DeleteMapping("/api/tenants/{externalTenantId}/tipos-de-ocorrencia/{id}")
    @Operation(summary = "Delete tipo de ocorrencia", description = "Deleta um tipo de ocorrência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo de ocorrência deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Tipo de ocorrência não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable Long id, @PathVariable String externalTenantId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        deleteTipoDeOcorrenciaService.execute(id, externalTenantId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
