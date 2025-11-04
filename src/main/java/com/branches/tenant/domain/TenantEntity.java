package com.branches.tenant.domain;

import com.branches.shared.config.envers.Auditable;
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
    private String nomeFantasia;
    @Column(length = 100, nullable = false)
    private String razaoSocial;

    @Column(length = 14, nullable = false, unique = true)
    private String cnpj;

    @Column(columnDefinition = "TEXT")
    private String logoUrl;

    @Column(length = 13, nullable = false)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo;
}
