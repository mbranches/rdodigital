package com.branches.obra.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.obra.dto.request.AdicionaUserToObraRequest;
import com.branches.obra.service.AddUserToObraService;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Obra")
public class AddUserToObraController {
    private final AddUserToObraService addUserToObraService;

    @PostMapping("/api/tenants/{tenantExternalId}/obras/{obraExternalId}/users")
    @Operation(summary = "Add user to obra", description = "Adiciona um usuário a uma obra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou obra não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@PathVariable String tenantExternalId,
                                        @PathVariable String obraExternalId,
                                        @RequestBody AdicionaUserToObraRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        addUserToObraService.execute(request, obraExternalId, tenantExternalId, userTenants);

        return ResponseEntity.noContent().build();
    }
}
