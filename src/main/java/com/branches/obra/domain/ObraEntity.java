package com.branches.obra.domain;

import com.branches.domain.GrupoDeObraEntity;
import com.branches.shared.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObraEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(unique = true, nullable = false)
    private String idExterno = UUID.randomUUID().toString();

    @Column(length = 100, nullable = false)
    private String nome;
    @Column(length = 100)
    private String responsavel;

    @Column(length = 100)
    private String contratante;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContratoDeObra tipoContrato;

    @Column(nullable = false)
    private LocalDate dataInicio;
    @Column(nullable = false)
    private LocalDate dataPrevistaFim;

    @Column(length = 100)
    private String numeroContrato;

    @Column(length = 200)
    private String endereco;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(columnDefinition = "TEXT")
    private String capaUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusObra status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMaoDeObraDeObra tipoMaoDeObra;
    @ManyToOne
    @JoinColumn(name = "grupo_de_obra_id")
    private GrupoDeObraEntity grupo;

    @Column(nullable = false)
    private Boolean ativo;
}
