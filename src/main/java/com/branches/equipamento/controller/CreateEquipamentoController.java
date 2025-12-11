package com.branches.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.equipamento.dto.request.CreateEquipamentoRequest;
import com.branches.equipamento.dto.response.CreateEquipamentoResponse;
import com.branches.equipamento.service.CreateEquipamentoService;
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
@Tag(name = "Equipamento")
public class CreateEquipamentoController {
    private final CreateEquipamentoService createEquipamentoService;

    @PostMapping("/api/tenants/{tenantExternalId}/equipamentos")
    @Operation(summary = "Create equipamento", description = "Cria um novo equipamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateEquipamentoResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateEquipamentoRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateEquipamentoResponse response = createEquipamentoService.execute(tenantExternalId, request, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
