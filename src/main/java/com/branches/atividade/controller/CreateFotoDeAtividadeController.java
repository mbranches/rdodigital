package com.branches.atividade.controller;

import com.branches.atividade.dto.request.CreateFotoDeAtividadeRequest;
import com.branches.atividade.dto.response.FotoDeAtividadeResponse;
import com.branches.atividade.service.CreateFotoDeAtividadeService;
import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Atividade")
@RequiredArgsConstructor
@RestController
public class CreateFotoDeAtividadeController {
    private final CreateFotoDeAtividadeService createFotoDeAtividadeService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/atividades/{atividadeId}/fotos")
    public ResponseEntity<FotoDeAtividadeResponse> execute(
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId,
            @PathVariable Long atividadeId,
            @RequestBody @Valid CreateFotoDeAtividadeRequest request
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        FotoDeAtividadeResponse response = createFotoDeAtividadeService.execute(tenantExternalId, relatorioExternalId, atividadeId, request, userTenants);

        return ResponseEntity.ok(response);
    }
}
