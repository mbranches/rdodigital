package com.branches.domain;

import com.branches.domain.enums.AssinaturaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AssinaturaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;

    @ManyToOne
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoEntity plano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssinaturaStatus status;

    private LocalDate dataInicio;
    private LocalDate dataFim;
}
