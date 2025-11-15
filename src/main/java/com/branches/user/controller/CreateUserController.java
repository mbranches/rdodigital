package com.branches.user.controller;

import com.branches.config.security.UserTenantsContext;
import com.branches.user.dto.request.CreateUserRequest;
import com.branches.user.dto.response.CreateUserResponse;
import com.branches.user.service.CreateUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CreateUserController {
    private final CreateUserService createUserService;

    @PostMapping("/api/users")
    public ResponseEntity<CreateUserResponse> execute(@RequestBody @Valid CreateUserRequest request) {
        Boolean userIsAdmin = UserTenantsContext.getUserIsAdmin();

        CreateUserResponse response = createUserService.execute(request, userIsAdmin);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
