package com.branches.auth.service;

import com.branches.auth.domain.RefreshTokenEntity;
import com.branches.auth.domain.enums.LoginType;
import com.branches.auth.dto.ClientInfo;
import com.branches.auth.dto.request.RefreshTokenRequest;
import com.branches.auth.dto.response.RefreshTokenResponse;
import com.branches.auth.repository.RefreshTokenRepository;
import com.branches.exception.NotFoundException;
import com.branches.user.domain.UserEntity;
import com.branches.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final LoginHistoryService loginHistoryService;
    @Value("${refresh-token.expiration}")
    private Long expiration;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generate(Long userId) {
        String refreshToken = UUID.randomUUID().toString();

        Instant refreshTokenExpiration = Instant.now().plusSeconds(expiration);

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .token(refreshToken)
                .userId(userId)
                .expiracao(refreshTokenExpiration)
                .build();

        refreshTokenRepository.save(entity);

        return refreshToken;
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request, ClientInfo clientInfo) {
        RefreshTokenEntity entity = refreshTokenRepository.findByTokenAndIsRevogadoIsFalse(request.refreshToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Long userId = entity.getUserId();

        UserEntity user = userRepository.findByIdAndAtivoIsTrue(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = generate(userId);

        entity.revogar();

        refreshTokenRepository.save(entity);

        loginHistoryService.registerLogin(userId, accessToken, LoginType.REFRESH, clientInfo);

        return new RefreshTokenResponse(accessToken, refreshToken);
    }
}
