package com.branches.relatorio.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.dto.response.RelatorioResponse;
import com.branches.relatorio.service.ListarRelatoriosService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.utils.PageResponse;
import com.branches.utils.PageableRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Relatorio")
public class ListarRelatoriosController {
    private final ListarRelatoriosService listarRelatoriosService;

    @GetMapping("/api/tenants/{tenantExternalId}/relatorios")
    @Operation(summary = "Listar relatorios", description = "Lista todos os relatórios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de relatórios recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PageResponse<RelatorioResponse>> execute(@PathVariable String tenantExternalId,
                                                                   @Valid PageableRequest pageableRequest) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PageResponse<RelatorioResponse> response = listarRelatoriosService.execute(tenantExternalId, userTenants, pageableRequest);

        return ResponseEntity.ok(response);
    }
}
