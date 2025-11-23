package com.branches.equipamento.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.equipamento.dto.request.CreateEquipamentoDeRelatorioRequest;
import com.branches.equipamento.dto.response.CreateEquipamentoDeRelatorioResponse;
import com.branches.equipamento.service.CreateEquipamentoDeRelatorioService;
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
public class CreateEquipamentoDeRelatorioController {
    private final CreateEquipamentoDeRelatorioService createEquipamentoDeRelatorioService;

    @PostMapping("/api/tenants/{tenantExternalId}/relatorios/{relatorioExternalId}/equipamentos")
    public ResponseEntity<CreateEquipamentoDeRelatorioResponse> execute(
            @RequestBody @Valid CreateEquipamentoDeRelatorioRequest request,
            @PathVariable String tenantExternalId,
            @PathVariable String relatorioExternalId
    ) {
        List<UserTenantEntity> userTenants = UserTenantsContext.getUserTenants();

        CreateEquipamentoDeRelatorioResponse response = createEquipamentoDeRelatorioService.execute(request, relatorioExternalId, tenantExternalId, userTenants);

        return ResponseEntity.ok().body(response);
    }
}
