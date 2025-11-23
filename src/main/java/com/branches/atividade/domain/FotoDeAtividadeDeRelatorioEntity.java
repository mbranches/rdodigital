package com.branches.atividade.domain;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FotoDeAtividadeDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;
    @ManyToOne
    @JoinColumn(name = "atividade_de_relatorio_id", nullable = false)
    private AtividadeDeRelatorioEntity atividadeDeRelatorio;

    //todo: implementar
}
