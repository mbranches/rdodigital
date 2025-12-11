package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.maodeobra.dto.request.CreateGrupoMaoDeObraRequest;
import com.branches.maodeobra.dto.response.CreateGrupoMaoDeObraResponse;
import com.branches.maodeobra.service.CreateGrupoMaoDeObraService;
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
public class CreateGrupoMaoDeObraController {
    private final CreateGrupoMaoDeObraService createGrupoMaoDeObraService;

    @PostMapping("/api/tenants/{tenantExternalId}/grupos-de-mao-de-obra")
    @Operation(summary = "Create grupo mao de obra", description = "Cria um novo grupo de mão de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grupo de mão de obra criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateGrupoMaoDeObraResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateGrupoMaoDeObraRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateGrupoMaoDeObraResponse response = createGrupoMaoDeObraService.execute(tenantExternalId, request, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
