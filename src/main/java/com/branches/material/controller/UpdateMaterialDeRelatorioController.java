package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.request.UpdateMaterialDeRelatorioRequest;
import com.branches.material.service.UpdateMaterialDeRelatorioService;
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
@Tag(name = "Material")
public class UpdateMaterialDeRelatorioController {
    private final UpdateMaterialDeRelatorioService updateMaterialDeRelatorioService;

    @PutMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/materiais/{id}")
    @Operation(summary = "Update material de relatorio", description = "Atualiza um material de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Material de relatório atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateMaterialDeRelatorioRequest request, @PathVariable String tenantExternalId, @PathVariable String relatorioExternalId, @PathVariable Long id) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateMaterialDeRelatorioService.execute(request, id, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
