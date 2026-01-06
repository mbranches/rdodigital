package com.branches.plano.controller;

import com.branches.plano.dto.request.CreatePlanoRequest;
import com.branches.plano.dto.response.PlanoResponse;
import com.branches.plano.service.CreatePlanoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Plano")
@RequiredArgsConstructor
@RestController
public class CreatePlanoController {
    private final CreatePlanoService createPlanoService;

    @Operation(summary = "Create plano", description = "Cria um plano (somente admins do sistema)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plano criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/api/planos")
    public ResponseEntity<PlanoResponse> execute(@RequestBody @Valid CreatePlanoRequest request) {
        PlanoResponse response = createPlanoService.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
