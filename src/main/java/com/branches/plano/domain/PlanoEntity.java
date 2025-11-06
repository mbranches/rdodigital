package com.branches.plano.domain;

import com.branches.config.envers.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    private Integer duracaoMeses;

    private Integer limiteUsuarios;

    private Integer limiteObras;
}
