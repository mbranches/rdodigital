package com.branches.obra.domain;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class LogoDeRelatorioEntity {
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
