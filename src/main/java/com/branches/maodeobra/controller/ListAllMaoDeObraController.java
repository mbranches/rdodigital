package com.branches.maodeobra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.maodeobra.dto.response.MaoDeObraResponse;
import com.branches.maodeobra.service.ListAllMaoDeObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "MaoDeObra")
public class ListAllMaoDeObraController {
    private final ListAllMaoDeObraService listAllMaoDeObraService;

    @GetMapping("api/tenants/{tenantExternalId}/mao-de-obra")
    @Operation(summary = "List all mao de obra", description = "Lista todas as mãos de obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mãos de obra recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<MaoDeObraResponse>> execute(@PathVariable String tenantExternalId, @RequestParam TipoMaoDeObra tipoMaoDeObra) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<MaoDeObraResponse> response = listAllMaoDeObraService.execute(tenantExternalId, tipoMaoDeObra, userTenants);

        return ResponseEntity.ok(response);
    }
}
