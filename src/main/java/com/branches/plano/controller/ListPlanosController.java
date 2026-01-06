package com.branches.plano.controller;

import com.branches.plano.dto.response.PlanoResponse;
import com.branches.plano.service.ListPlanosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Plano")
@RequiredArgsConstructor
@RestController
public class ListPlanosController {
    private final ListPlanosService listPlanosService;

    @Operation(summary = "List planos", description = "Lista todos os planos disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Planos recuperados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/api/planos")
    public ResponseEntity<List<PlanoResponse>> execute() {
        List<PlanoResponse> response = listPlanosService.execute();

        return ResponseEntity.ok(response);
    }
}
