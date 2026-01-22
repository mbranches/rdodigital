package com.branches.comentarios.controller;

import com.branches.comentarios.dto.response.ComentarioDeRelatorioResponse;
import com.branches.comentarios.service.ListComentariosDeObraPorRelatorioService;
import com.branches.config.security.UserTenantsContext;
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
@Tag(name = "Comentarios")
public class ListComentariosDeObraPorRelatorioController {
    private final ListComentariosDeObraPorRelatorioService listComentariosDeObraPorRelatorioService;

    @GetMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/comentarios")
    @Operation(summary = "List comentarios de obra por relatorio", description = "Lista os comentários de uma obra agrupado por relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentários recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PageResponse<ItemPorRelatorioResponse<ComentarioDeRelatorioResponse>>> execute(@PathVariable String tenantExternalId,
                                                                                                          @PathVariable String obraExternalId,
                                                                                                          @Valid PageableRequest pageRequest) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        PageResponse<ItemPorRelatorioResponse<ComentarioDeRelatorioResponse>> response = listComentariosDeObraPorRelatorioService.execute(tenantExternalId, obraExternalId, userTenants, pageRequest);

        return ResponseEntity.ok(response);
    }
}
