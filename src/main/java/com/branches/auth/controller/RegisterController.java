package com.branches.auth.controller;

import com.branches.auth.dto.request.RegisterRequest;
import com.branches.auth.service.RegisterService;
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
public class RegisterController {
    private final RegisterService registerService;

    @PostMapping("/api/auth/register")
    @Operation(summary = "Register", description = "Registra um novo usuário(A senha deve conter no minimo 6 digitos e só pode conter letras, numeros e _)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "409", description = "Usuário já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> execute(@RequestBody @Valid RegisterRequest request) {
        registerService.execute(request);

        return ResponseEntity.noContent().build();
    }
}
