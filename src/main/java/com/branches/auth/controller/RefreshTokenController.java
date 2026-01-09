package com.branches.auth.controller;

import com.branches.auth.dto.ClientInfo;
import com.branches.auth.dto.request.RefreshTokenRequest;
import com.branches.auth.dto.response.RefreshTokenResponse;
import com.branches.auth.service.ClientInfoService;
import com.branches.auth.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Auth")
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;
    private final ClientInfoService clientInfoService;

    @PostMapping("/api/auth/refresh")
    @Operation(summary = "Refresh", description = "Atualiza token do user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atualização realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<RefreshTokenResponse> execute(@Valid @RequestBody RefreshTokenRequest request,
                                                        HttpServletRequest httpServletRequest) {
        ClientInfo clientInfo = clientInfoService.execute(httpServletRequest);

        RefreshTokenResponse response = refreshTokenService.refresh(request, clientInfo);

        return ResponseEntity.ok(response);
    }
}
