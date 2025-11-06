package com.branches.obra.controller;

import com.branches.auth.model.UserDetailsImpl;
import com.branches.obra.dto.request.CreateObraRequest;
import com.branches.obra.dto.response.CreateObraResponse;
import com.branches.obra.service.CreateObraService;
import com.branches.user.domain.UserTenantEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<CreateObraResponse> execute(@PathVariable String tenantIdExternal, @Valid @RequestBody CreateObraRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserTenantEntity> userTenants = userDetails.getUser().getUserTenantEntities();

        CreateObraResponse response = createObraService.execute(request, tenantIdExternal, userTenants);

        return ResponseEntity.ok(response);
    }
}
