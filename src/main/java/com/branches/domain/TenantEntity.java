package com.branches.domain;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
