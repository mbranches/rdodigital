package com.branches.maodeobra.domain;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Setter
@Getter
@Entity
public class MaoDeObraEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String funcao;

    @ManyToOne
    @JoinColumn(name = "grupo_mao_de_obra_id", nullable = false)
    private GrupoMaoDeObraEntity grupo;

    @Column(length = 100)
    private String nome;

    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Integer minutosIntervalo;
    private LocalTime horasTrabalhadas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMaoDeObra tipo;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    public void desativar() {
        this.ativo = false;
    }
}
