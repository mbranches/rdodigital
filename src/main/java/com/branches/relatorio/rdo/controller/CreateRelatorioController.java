package com.branches.relatorio.rdo.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.relatorio.rdo.dto.request.CreateRelatorioRequest;
import com.branches.relatorio.rdo.dto.response.CreateRelatorioResponse;
import com.branches.relatorio.rdo.service.CreateRelatorioService;
import com.branches.usertenant.domain.UserTenantEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CreateRelatorioController {
    private final CreateRelatorioService createRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios")
    public ResponseEntity<CreateRelatorioResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateRelatorioRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateRelatorioResponse response = createRelatorioService.execute(request, tenantExternalId, userTenants);

        return ResponseEntity.ok(response);
    }
}
