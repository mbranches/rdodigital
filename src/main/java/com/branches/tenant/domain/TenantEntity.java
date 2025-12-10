package com.branches.tenant.domain;

import com.branches.config.envers.Auditable;
import com.branches.tenant.domain.enums.SegmentoTenant;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TenantEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Builder.Default
    @Column(nullable = false, unique = true)
    private String idExterno = UUID.randomUUID().toString();

    @Column(length = 100, nullable = false)
    private String nome;
    @Column(length = 100, nullable = false)
    private String razaoSocial;

    @Column(length = 14, nullable = false, unique = true)
    private String cnpj;

    @Column(columnDefinition = "TEXT")
    private String logoUrl;

    @Column(length = 13, nullable = false)
    private String telefone;

    @Column(nullable = false)
    private Long userResponsavelId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SegmentoTenant segmento;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;
}
