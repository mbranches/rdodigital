package com.branches.configuradores.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.configuradores.domain.enums.RecorrenciaRelatorio;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ModeloDeRelatorioEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String titulo;

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

    @Column(nullable = false)
    private Boolean isDefault;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;
}
