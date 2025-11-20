package com.branches.configuradores.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.configuradores.dto.request.CreateModeloDeRelatorioRequest;
import com.branches.configuradores.dto.response.CreateModeloDeRelatorioResponse;
import com.branches.configuradores.service.CreateModeloDeRelatorioService;
import com.branches.usertenant.domain.UserTenantEntity;
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
public class CreateModeloDeRelatorioController {
    private final CreateModeloDeRelatorioService createModeloDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/configuradores/modelos-de-relatorio")
    public ResponseEntity<CreateModeloDeRelatorioResponse> execute(@PathVariable String tenantExternalId,
                                                                   @RequestBody CreateModeloDeRelatorioRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateModeloDeRelatorioResponse response = createModeloDeRelatorioService.execute(request, tenantExternalId, userTenants);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
