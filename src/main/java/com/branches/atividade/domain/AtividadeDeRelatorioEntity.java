package com.branches.atividade.domain;

import com.branches.relatorio.domain.CampoPersonalizadoEntity;
import com.branches.maodeobra.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.atividade.domain.enums.StatusAtividade;
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
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "atividade_relatorio_campos_personalizados",
            joinColumns = @JoinColumn(name = "atividade_relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "campo_personalizado_id")
    )
    private List<CampoPersonalizadoEntity> camposPersonalizados;
    @OneToMany(mappedBy = "atividadeDeRelatorio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MaoDeObraDeAtividadeDeRelatorioEntity> maoDeObra;
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
}
