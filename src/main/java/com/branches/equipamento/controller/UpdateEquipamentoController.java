package com.branches.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.equipamento.dto.request.UpdateEquipamentoRequest;
import com.branches.equipamento.service.UpdateEquipamentoService;
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
@Tag(name = "Equipamento")
public class UpdateEquipamentoController {
    private final UpdateEquipamentoService updateEquipamentoService;

    @PutMapping("/api/tenants/{tenantExternalId}/equipamentos/{id}")
    @Operation(summary = "Update equipamento", description = "Atualiza um equipamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Equipamento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId, @PathVariable Long id, @RequestBody @Valid UpdateEquipamentoRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        updateEquipamentoService.execute(id, request, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
