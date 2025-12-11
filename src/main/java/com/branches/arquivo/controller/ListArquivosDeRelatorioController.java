package com.branches.arquivo.controller;

import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.response.ArquivoResponse;
import com.branches.arquivo.service.ListArquivosDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Arquivo")
public class ListArquivosDeRelatorioController {

    private final ListArquivosDeRelatorioService listArquivosDeRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/arquivos")
    @Operation(summary = "List arquivos de relatorio", description = "Lista os arquivos de um relatório por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de arquivos recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ArquivoResponse>> execute(@PathVariable String tenantExternalId,
                                                         @PathVariable String relatorioExternalId,
                                                         @RequestParam TipoArquivo tipo) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        List<ArquivoResponse> response = listArquivosDeRelatorioService.execute(relatorioExternalId, tenantExternalId, tipo, userTenants);

        return ResponseEntity.ok(response);
    }

}
