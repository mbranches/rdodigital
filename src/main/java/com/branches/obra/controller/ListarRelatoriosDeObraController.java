package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.service.ListarRelatoriosDeObraService;
import com.branches.relatorio.dto.response.RelatorioResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Obra")
public class ListarRelatoriosDeObraController {
    private final ListarRelatoriosDeObraService listarRelatoriosDeObraService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/relatorios")
    @Operation(summary = "Listar relatorios de obra", description = "Lista os relatórios de uma obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de relatórios recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Obra não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PageResponse<RelatorioResponse>> execute(@PathVariable String tenantExternalId,
                                                                   @PathVariable String obraExternalId,
                                                                   @Valid PageableRequest pageableRequest
                                                           ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PageResponse<RelatorioResponse> response = listarRelatoriosDeObraService.execute(obraExternalId, tenantExternalId, userTenants, pageableRequest);

        return ResponseEntity.ok(response);
    }
}
