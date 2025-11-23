package com.branches.relatorio.domain;

import com.branches.relatorio.domain.enums.TipoMaterial;
import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MaterialDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    private String quantidade;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMaterial tipoMaterial;

    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
}
