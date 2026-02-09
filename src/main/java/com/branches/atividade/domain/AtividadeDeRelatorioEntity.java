package com.branches.atividade.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.ocorrencia.domain.OcorrenciaDeRelatorioEntity;
import com.branches.maodeobra.domain.MaoDeObraDeAtividadeDeRelatorioEntity;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.atividade.domain.enums.StatusAtividade;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AtividadeDeRelatorioEntity extends AuditableTenantOwned {
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
    private Integer minutosTotais;
    @OneToMany(mappedBy = "atividadeDeRelatorio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AtividadeDeRelatorioCampoPersonalizadoEntity> camposPersonalizados;
    @OneToMany(mappedBy = "atividadeDeRelatorio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MaoDeObraDeAtividadeDeRelatorioEntity> maoDeObra;
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;

    @OneToMany(mappedBy = "atividadeVinculada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OcorrenciaDeRelatorioEntity> ocorrencias; //Usado somente pro delete em cascata

    @OneToMany(mappedBy = "atividade", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<FotoDeAtividadeEntity> fotos;
}
