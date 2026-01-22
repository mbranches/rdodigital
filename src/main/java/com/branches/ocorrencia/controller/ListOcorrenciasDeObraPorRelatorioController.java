package com.branches.ocorrencia.controller;
import com.branches.config.security.UserTenantsContext;
import com.branches.ocorrencia.dto.response.OcorrenciaDeRelatorioResponse;
import com.branches.ocorrencia.service.ListOcorrenciasDeObraPorRelatorioService;
import com.branches.relatorio.dto.response.ItemPorRelatorioResponse;
import com.branches.shared.pagination.PageResponse;
import com.branches.shared.pagination.PageableRequest;
import com.branches.usertenant.domain.UserTenantEntity;
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
@Tag(name = "Ocorrencia")
public class ListOcorrenciasDeObraPorRelatorioController {

    private final ListOcorrenciasDeObraPorRelatorioService listOcorrenciasDeObraPorRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/ocorrencias")
    @Operation(summary = "List ocorrencias de obra por relatorio", description = "Lista as ocorrencias de uma obra agrupado por relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ocorrencias recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PageResponse<ItemPorRelatorioResponse<OcorrenciaDeRelatorioResponse>>> execute(@PathVariable String tenantExternalId,
                                                                                                         @PathVariable String obraExternalId,
                                                                                                         @Valid PageableRequest pageRequest) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PageResponse<ItemPorRelatorioResponse<OcorrenciaDeRelatorioResponse>> response = listOcorrenciasDeObraPorRelatorioService.execute(tenantExternalId, obraExternalId, userTenants, pageRequest);

        return ResponseEntity.ok(response);
    }
}
