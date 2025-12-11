package com.branches.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.equipamento.dto.response.EquipamentoResponse;
import com.branches.equipamento.service.ListAllEquipamentosService;
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
@Tag(name = "Equipamento")
public class ListAllEquipamentosController {
    private final ListAllEquipamentosService listAllEquipamentosService;

    @GetMapping("/api/tenants/{externalTenantId}/equipamentos")
    @Operation(summary = "List all equipamentos", description = "Lista todos os equipamentos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de equipamentos recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<EquipamentoResponse>> execute(@PathVariable String externalTenantId) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<EquipamentoResponse> response = listAllEquipamentosService.execute(externalTenantId, userTenants);

        return ResponseEntity.ok(response);
    }
}
