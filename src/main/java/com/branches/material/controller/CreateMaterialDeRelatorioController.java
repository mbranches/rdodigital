package com.branches.material.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.material.dto.request.CreateMaterialDeRelatorioRequest;
import com.branches.material.dto.response.CreateMaterialDeRelatorioResponse;
import com.branches.material.service.CreateMaterialDeRelatorioService;
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
public class CreateMaterialDeRelatorioController {
    private final CreateMaterialDeRelatorioService createMaterialDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/materiais")
    public ResponseEntity<CreateMaterialDeRelatorioResponse> execute(
            @RequestBody @Valid CreateMaterialDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateMaterialDeRelatorioResponse response = createMaterialDeRelatorioService.execute(request, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
