package com.branches.relatorio.rdo.domain;

import com.branches.relatorio.rdo.domain.enums.StatusAtividade;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AtividadeDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String descricao;
    private Integer quantidadeRealizada;
    @Column(length = 10)
    private String unidadeMedida;
    @Column(precision = 5, scale = 2)
    private BigDecimal porcentagemConcluida;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtividade status;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalTime totalHoras;
    @ManyToMany
    @JoinTable(
            name = "atividade_relatorio_campos_personalizados",
            joinColumns = @JoinColumn(name = "atividade_relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "campo_personalizado_id")
    )
    private List<CampoPersonalizadoEntity> camposPersonalizados;
    @ManyToMany
    @JoinTable(
            name = "atividade_relatorio_mao_de_obra",
            joinColumns = @JoinColumn(name = "atividade_relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "mao_de_obra_id")
    )
    private List<MaoDeObraEntity> maosDeObra;
}
