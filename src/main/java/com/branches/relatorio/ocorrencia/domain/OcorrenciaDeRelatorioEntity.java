package com.branches.relatorio.ocorrencia.domain;

import com.branches.relatorio.rdo.domain.AtividadeDeRelatorioEntity;
import com.branches.relatorio.rdo.domain.CampoPersonalizadoEntity;
import com.branches.relatorio.rdo.domain.RelatorioEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OcorrenciaDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
    @ManyToOne
    @JoinColumn(name = "ocorrencia_id", nullable = false)
    private OcorrenciaEntity ocorrencia;
    @Column(nullable = false)
    private String descricao;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalTime totalHoras;
    @ManyToOne
    @JoinColumn(name = "atividade_id")
    private AtividadeDeRelatorioEntity atividadeVinculada;
    @ManyToMany
    @JoinTable(
            name = "ocorrencia_relatorio_campos_personalizados",
            joinColumns = @JoinColumn(name = "ocorrencia_relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "campo_personalizado_id")
    )
    private List<CampoPersonalizadoEntity> camposPersonalizados;
}
