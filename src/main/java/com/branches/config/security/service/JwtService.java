package com.branches.config.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.branches.domain.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    private final String issuer = "meudiariodeobras-backend";

    public String generateToken(UserEntity user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(creationDate())
                .withExpiresAt(expirationDate())
                .withSubject(user.getIdExterno())
                .sign(algorithm);
    }

    public String validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant creationDate() {
        return Instant.now();
    }

    private Instant expirationDate() {
        int hoursValid = 1;
        return Instant.now().plusSeconds(hoursValid * 3600);
    }
}

