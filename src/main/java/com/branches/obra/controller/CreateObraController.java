package com.branches.obra.controller;

import com.branches.obra.dto.request.CreateObraRequest;
import com.branches.obra.dto.response.CreateObraResponse;
import com.branches.obra.service.CreateObraService;
import com.branches.security.config.TenantContext;
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
public class CreateObraController {
    private final CreateObraService createObraService;

    @PostMapping("/api/tenants/{tenantIdExternal}/obras")
    public ResponseEntity<CreateObraResponse> execute(@PathVariable String tenantIdExternal, @Valid @RequestBody CreateObraRequest request) {
        List<Long> userTenantIds = TenantContext.getTenantIds();

        CreateObraResponse response = createObraService.execute(request, tenantIdExternal, userTenantIds);

        return ResponseEntity.ok(response);
    }
}
