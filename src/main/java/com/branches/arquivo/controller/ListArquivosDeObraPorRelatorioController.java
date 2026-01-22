package com.branches.arquivo.controller;

import com.branches.arquivo.domain.enums.TipoArquivo;
import com.branches.arquivo.dto.response.ArquivosDeObraPorRelatorioResponse;
import com.branches.arquivo.service.ListArquivosDeObraPorRelatorioService;
import com.branches.config.security.UserTenantsContext;
import com.branches.shared.pagination.PageResponse;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.shared.pagination.PageableRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Arquivo")
@RequiredArgsConstructor
@RestController
public class ListArquivosDeObraPorRelatorioController {
    private final ListArquivosDeObraPorRelatorioService listArquivosDeObraPorRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/arquivos")
    @Operation(summary = "List arquivos de obra por relatorio", description = "Lista os arquivos de uma obra agrupado por relatório e por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de arquivos recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PageResponse<ArquivosDeObraPorRelatorioResponse>> execute(@PathVariable String tenantExternalId,
                                                                                    @PathVariable String obraExternalId,
                                                                                    @RequestParam @NotNull(message = "O parâmetro 'tipo' é obrigatório") TipoArquivo tipo,
                                                                                    @Valid PageableRequest pageRequest) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PageResponse<ArquivosDeObraPorRelatorioResponse> response = listArquivosDeObraPorRelatorioService.execute(tenantExternalId, obraExternalId, tipo, userTenants, pageRequest);

        return ResponseEntity.ok(response);
    }
}
