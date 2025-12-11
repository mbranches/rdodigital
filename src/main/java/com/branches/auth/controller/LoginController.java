package com.branches.auth.controller;

import com.branches.auth.dto.request.LoginRequest;
import com.branches.auth.dto.response.LoginResponse;
import com.branches.auth.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Auth")
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/api/auth/login")
    @Operation(summary = "Login", description = "Realiza o login de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<LoginResponse> execute(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.execute(request);

        return ResponseEntity.ok(response);
    }
}
