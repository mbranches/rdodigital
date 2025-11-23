package com.branches.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.equipamento.dto.request.CreateEquipamentoRequest;
import com.branches.equipamento.dto.response.CreateEquipamentoResponse;
import com.branches.equipamento.service.CreateEquipamentoService;
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
public class CreateEquipamentoController {
    private final CreateEquipamentoService createEquipamentoService;

    @PostMapping("/api/tenants/{tenantExternalId}/equipamentos")
    public ResponseEntity<CreateEquipamentoResponse> execute(@PathVariable String tenantExternalId, @RequestBody @Valid CreateEquipamentoRequest request) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateEquipamentoResponse response = createEquipamentoService.execute(tenantExternalId, request, userTenants);

        return ResponseEntity.ok().body(response);
    }
}
