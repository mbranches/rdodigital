package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.request.CreateMaterialDeRelatorioRequest;
import com.branches.material.dto.response.CreateMaterialDeRelatorioResponse;
import com.branches.material.service.CreateMaterialDeRelatorioService;
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
@Tag(name = "Material")
public class CreateMaterialDeRelatorioController {
    private final CreateMaterialDeRelatorioService createMaterialDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/materiais")
    @Operation(summary = "Create material de relatorio", description = "Cria um novo material de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Material de relatório criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateMaterialDeRelatorioResponse> execute(
            @RequestBody @Valid CreateMaterialDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateMaterialDeRelatorioResponse response = createMaterialDeRelatorioService.execute(request, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
