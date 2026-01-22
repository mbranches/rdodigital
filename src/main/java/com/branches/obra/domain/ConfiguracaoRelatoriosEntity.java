package com.branches.obra.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ConfiguracaoRelatoriosEntity extends AuditableTenantOwned {
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "configuracaoRelatorios")
    private List<ConfiguracaoDeAssinaturaDeRelatorioEntity> configuracoesDeAssinaturaDeRelatorio;

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

    public static ConfiguracaoRelatoriosEntity by(ModeloDeRelatorioEntity modeloDeRelatorioDefault, String urlLogoTenant, String nome, String nomeClienteObra, Long tenantId) {
        LogoDeRelatorioEntity logoDeRelatorioDefault = LogoDeRelatorioEntity.builder()
                .url(urlLogoTenant)
                .exibir(urlLogoTenant != null && !urlLogoTenant.isBlank())
                .isLogoDoTenant(true)
                .tenantId(tenantId)
                .build();

        var config = ConfiguracaoRelatoriosEntity.builder()
                .modeloDeRelatorio(modeloDeRelatorioDefault)
                .recorrenciaRelatorio(modeloDeRelatorioDefault.getRecorrenciaRelatorio())
                .logoDeRelatorio1(logoDeRelatorioDefault)
                .logoDeRelatorio2(createLogoDeRelatorioEmpty(tenantId))
                .logoDeRelatorio3(createLogoDeRelatorioEmpty(tenantId))
                .showCondicaoClimatica(modeloDeRelatorioDefault.getShowCondicaoClimatica())
                .showMaoDeObra(modeloDeRelatorioDefault.getShowMaoDeObra())
                .showEquipamentos(modeloDeRelatorioDefault.getShowEquipamentos())
                .showAtividades(modeloDeRelatorioDefault.getShowAtividades())
                .showOcorrencias(modeloDeRelatorioDefault.getShowOcorrencias())
                .showComentarios(modeloDeRelatorioDefault.getShowComentarios())
                .showMateriais(modeloDeRelatorioDefault.getShowMateriais())
                .showHorarioDeTrabalho(modeloDeRelatorioDefault.getShowHorarioDeTrabalho())
                .showFotos(modeloDeRelatorioDefault.getShowFotos())
                .showVideos(modeloDeRelatorioDefault.getShowVideos())
                .tenantId(tenantId)
                .build();

        boolean clienteNameIsNotEmpty = nomeClienteObra != null && !nomeClienteObra.isBlank();
        ConfiguracaoDeAssinaturaDeRelatorioEntity assinaturaTenantDefault = ConfiguracaoDeAssinaturaDeRelatorioEntity.builder()
                .nomeAssinante(clienteNameIsNotEmpty ? nome : "Assinatura")
                .configuracaoRelatorios(config)
                .tenantId(tenantId)
                .build();

        ConfiguracaoDeAssinaturaDeRelatorioEntity assinaturaClienteDefault = ConfiguracaoDeAssinaturaDeRelatorioEntity.builder()
                .nomeAssinante(clienteNameIsNotEmpty ? nomeClienteObra : "Assinatura")
                .configuracaoRelatorios(config)
                .tenantId(tenantId)
                .build();


        List<ConfiguracaoDeAssinaturaDeRelatorioEntity> assinaturasDefault = List.of(assinaturaTenantDefault, assinaturaClienteDefault);
        config.setConfiguracoesDeAssinaturaDeRelatorio(assinaturasDefault);

        return config;
    }

    private static LogoDeRelatorioEntity createLogoDeRelatorioEmpty(Long tenantId) {
        return LogoDeRelatorioEntity.builder()
                .url(null)
                .exibir(false)
                .tenantId(tenantId)
                .isLogoDoTenant(false)
                .build();
    }
}
