package com.branches.auth.domain;

import com.branches.config.envers.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RefreshTokenEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private Instant expiracao;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRevogado = false;

    private LocalDateTime revogadoEm;

    public void revogar() {
        this.isRevogado = true;
        this.revogadoEm = LocalDateTime.now();
    }
}
