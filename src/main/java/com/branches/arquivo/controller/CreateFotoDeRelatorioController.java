package com.branches.arquivo.controller;

import com.branches.arquivo.dto.request.CreateFotoDeRelatorioRequest;
import com.branches.arquivo.dto.response.FotoDeRelatorioResponse;
import com.branches.arquivo.service.CreateFotoDeRelatorioService;
import com.branches.config.security.UserTenantsContext;
import com.branches.usertenant.domain.UserTenantEntity;
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
public class CreateFotoDeRelatorioController {
    private final CreateFotoDeRelatorioService createFotoDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/fotos")
    public ResponseEntity<FotoDeRelatorioResponse> execute(
            @RequestBody @Valid CreateFotoDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        FotoDeRelatorioResponse response = createFotoDeRelatorioService.execute(
                request,
                tenantExternalId,
                relatorioExternalId,
                userTenants
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
