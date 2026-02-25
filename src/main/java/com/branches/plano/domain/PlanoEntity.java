package com.branches.plano.domain;

import com.branches.config.envers.Auditable;
import com.branches.plano.domain.enums.RecorrenciaPlano;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PlanoEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal valor;

    @Column(nullable  = false)
    @Enumerated(EnumType.STRING)
    private RecorrenciaPlano recorrencia;

    @Column(nullable = false)
    private Integer limiteUsuarios;
    @Column(nullable = false)
    private Integer limiteObras;

    @Column(nullable = false)
    private String stripeProductId;
    @Column(nullable = false)
    private String stripePriceId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    public LocalDate calcularDataFim(LocalDate dataInicio) {
        return switch (recorrencia) {
            case MENSAL -> dataInicio.plusMonths(1);
            case ANUAL -> dataInicio.plusYears(1);
        };

    }
}
