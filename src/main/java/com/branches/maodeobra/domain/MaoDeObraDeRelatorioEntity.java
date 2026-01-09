package com.branches.maodeobra.domain;
import com.branches.maodeobra.domain.enums.PresencaMaoDeObra;
import com.branches.relatorio.domain.RelatorioEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MaoDeObraDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "mao_de_obra_id", nullable = false)
    private MaoDeObraEntity maoDeObra;
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
    @Column(nullable = false, length = 100)
    private String funcao;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PresencaMaoDeObra presenca;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer minutosIntervalo;
    private LocalTime horasTrabalhadas;
}
