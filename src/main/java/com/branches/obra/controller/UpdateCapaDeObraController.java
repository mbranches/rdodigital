package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.UpdateCapaDeObraRequest;
import com.branches.obra.service.UpdateCapaDeObraService;
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
@Tag(name = "Obra")
public class UpdateCapaDeObraController {
    private final UpdateCapaDeObraService updateCapaDeObraService;

    @PutMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/capa")
    @Operation(summary = "Update capa de obra", description = "Atualiza a capa de uma obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Capa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateCapaDeObraRequest request,
                                        @PathVariable String tenantExternalId,
                                        @PathVariable String obraExternalId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateCapaDeObraService.execute(request, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
