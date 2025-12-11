package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.CreateGrupoDeObraRequest;
import com.branches.obra.dto.response.CreateGrupoDeObraResponse;
import com.branches.obra.service.CreateGrupoDeObraService;
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
@Tag(name = "Obra")
public class CreateGrupoDeObraController {
    private final CreateGrupoDeObraService createGrupoDeObraService;

    @PostMapping("/api/tenants/{tenantExternalId}/grupos-de-obra")
    @Operation(summary = "Create grupo de obra", description = "Cria um novo grupo de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grupo de obra criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateGrupoDeObraResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateGrupoDeObraRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateGrupoDeObraResponse response = createGrupoDeObraService.execute(tenantExternalId, request, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
