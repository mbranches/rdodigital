package com.branches.user.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.user.domain.UserEntity;
import com.branches.user.dto.request.UpdateUserInfosRequest;
import com.branches.user.service.UpdateUserInfosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UpdateUserInfosController {
    private final UpdateUserInfosService updateUserInfosService;

    @PutMapping("/api/users/me")
    public ResponseEntity<Void> execute(@RequestBody @Valid UpdateUserInfosRequest request) {
        UserEntity user = UserTenantsContext.getUser();

        updateUserInfosService.execute(user, request);

        return ResponseEntity.noContent().build();
    }
}
