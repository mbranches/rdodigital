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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "logo_de_relatorio_1_id")
    private LogoDeRelatorioEntity logoDeRelatorio1;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "logo_de_relatorio_2_id")
    private LogoDeRelatorioEntity logoDeRelatorio2;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "logo_de_relatorio_3_id")
    private LogoDeRelatorioEntity logoDeRelatorio3;

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
    @Column(nullable = false)
    private Boolean showVideos;

    public static ConfiguracaoRelatoriosEntity by(ModeloDeRelatorioEntity modeloDeRelatorioDefault, String urlLogoTenant) {
        LogoDeRelatorioEntity logoDeRelatorioDefault = LogoDeRelatorioEntity.builder()
                .url(urlLogoTenant)
                .exibir(urlLogoTenant != null && !urlLogoTenant.isBlank())
                .isLogoDoTenant(true)
                .build();

        return ConfiguracaoRelatoriosEntity.builder()
                .modeloDeRelatorio(modeloDeRelatorioDefault)
                .recorrenciaRelatorio(modeloDeRelatorioDefault.getRecorrenciaRelatorio())
                .logoDeRelatorio1(logoDeRelatorioDefault)
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
