package com.branches.ocorrencia.domain;

import com.branches.relatorio.domain.CampoPersonalizadoEntity;
import com.branches.relatorio.domain.RelatorioEntity;
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

    @ManyToMany
    @JoinTable(
            name = "ocorrencia_relatorio_tipos_de_ocorrencia",
            joinColumns = @JoinColumn(name = "ocorrencia_relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_de_ocorrencia_id")
    )
    private List<TipoDeOcorrenciaEntity> tiposDeOcorrencia;

    @Column(nullable = false)
    private String descricao;

    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalTime totalHoras;

    @ManyToMany
    @JoinTable(
            name = "ocorrencia_relatorio_campos_personalizados",
            joinColumns = @JoinColumn(name = "ocorrencia_relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "campo_personalizado_id")
    )
    private List<CampoPersonalizadoEntity> camposPersonalizados;
}
