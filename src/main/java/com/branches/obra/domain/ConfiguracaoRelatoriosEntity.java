package com.branches.obra.domain;

import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ConfiguracaoRelatoriosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "modelo_de_relatorio_id", nullable = false)
    private ModeloDeRelatorioEntity modeloDeRelatorio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecorrenciaRelatorio recorrenciaRelatorio;

    @Column(nullable = false)
    private Boolean showCondicaoClimatica;
    @Column(nullable = false)
    private Boolean showMaoDeObra;
    @Column(nullable = false)
    private Boolean showEquipamentos;
    @Column(nullable = false)
    private Boolean showAtividades;
    @Column(nullable = false)
    private Boolean showOcorrencias;
    @Column(nullable = false)
    private Boolean showComentarios;
    @Column(nullable = false)
    private Boolean showMateriais;
    @Column(nullable = false)
    private Boolean showHorarioDeTrabalho;
    @Column(nullable = false)
    private Boolean showFotos;

    public static ConfiguracaoRelatoriosEntity by(ModeloDeRelatorioEntity modeloDeRelatorioDefault) {
        return ConfiguracaoRelatoriosEntity.builder()
                .modeloDeRelatorio(modeloDeRelatorioDefault)
                .recorrenciaRelatorio(modeloDeRelatorioDefault.getRecorrenciaRelatorio())
                .showCondicaoClimatica(modeloDeRelatorioDefault.getShowCondicaoClimatica())
                .showMaoDeObra(modeloDeRelatorioDefault.getShowMaoDeObra())
                .showEquipamentos(modeloDeRelatorioDefault.getShowEquipamentos())
                .showAtividades(modeloDeRelatorioDefault.getShowAtividades())
                .showOcorrencias(modeloDeRelatorioDefault.getShowOcorrencias())
                .showComentarios(modeloDeRelatorioDefault.getShowComentarios())
                .showMateriais(modeloDeRelatorioDefault.getShowMateriais())
                .showHorarioDeTrabalho(modeloDeRelatorioDefault.getShowHorarioDeTrabalho())
                .showFotos(modeloDeRelatorioDefault.getShowFotos())
                .build();
    }
}
