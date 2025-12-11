package com.branches.comentarios.controller;

import com.branches.comentarios.dto.request.CreateComentarioDeRelatorioRequest;
import com.branches.comentarios.dto.response.CreateComentarioDeRelatorioResponse;
import com.branches.comentarios.service.CreateComentarioDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
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
@Tag(name = "Comentarios")
public class CreateComentarioDeRelatorioController {
    private final CreateComentarioDeRelatorioService createComentarioDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/comentarios")
    @Operation(summary = "Create comentario de relatorio", description = "Cria um novo comentário de relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Relatório não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<CreateComentarioDeRelatorioResponse> execute(
            @RequestBody @Valid CreateComentarioDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId) {

        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateComentarioDeRelatorioResponse response = createComentarioDeRelatorioService.execute(
                request,
                relatorioExternalId,
                tenantExternalId,
                userTenants
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
