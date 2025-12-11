package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.CreateObraRequest;
import com.branches.obra.dto.response.CreateObraResponse;
import com.branches.obra.service.CreateObraService;
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
public class CreateObraController {
    private final CreateObraService createObraService;

    @PostMapping("/api/tenants/{tenantIdExternal}/obras")
    @Operation(summary = "Create obra", description = "Cria uma nova obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Obra criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateObraResponse> execute(@PathVariable String tenantIdExternal, @Valid @RequestBody CreateObraRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateObraResponse response = createObraService.execute(request, tenantIdExternal, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
