package com.branches.relatorio.rdo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ComentarioDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
    @ManyToMany
    @JoinTable(
        name = "comentario_campo_personalizado",
        joinColumns = @JoinColumn(name = "comentario_id"),
        inverseJoinColumns = @JoinColumn(name = "campo_personalizado_id")
    )
    private List<CampoPersonalizadoEntity> camposPersonalizados;
}
