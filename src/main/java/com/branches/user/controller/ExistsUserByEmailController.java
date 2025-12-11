package com.branches.user.controller;

import com.branches.user.dto.request.ExistsUserByEmailRequest;
import com.branches.user.dto.response.ExistsUserByEmailResponse;
import com.branches.user.service.ExistsUserByEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "User")
public class ExistsUserByEmailController {
    private final ExistsUserByEmailService existsUserByEmailService;

    @GetMapping("/api/users/exists-by-email")
    @Operation(summary = "Exists user by email", description = "Verifica se existe um usuário com o email informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ExistsUserByEmailResponse> execute(@RequestBody @Valid ExistsUserByEmailRequest request) {
        boolean response = existsUserByEmailService.execute(request.email());

        return ResponseEntity.ok(new ExistsUserByEmailResponse(response));
    }
}
