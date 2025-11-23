package com.branches.relatorio.domain;

import com.branches.maodeobra.domain.MaoDeObraEntity;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MaoDeObraDeAtividadeDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mao_de_obra_id", nullable = false)
    private MaoDeObraEntity maoDeObra;

    @Column(length = 100, nullable = false)
    private String funcao;

    @ManyToOne
    @JoinColumn(name = "atividade_de_relatorio_id", nullable = false)
    private AtividadeDeRelatorioEntity atividadeDeRelatorio;
}
