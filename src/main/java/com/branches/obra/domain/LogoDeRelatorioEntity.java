package com.branches.obra.domain;

import com.branches.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class LogoDeRelatorioEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isLogoDoTenant = false;

    @Column(nullable = false)
    private Boolean exibir;
}
