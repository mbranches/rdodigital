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
    private String titulo;
    @Enumerated(EnumType.STRING)
    private RecorrenciaRelatorio recorrenciaRelatorio;
    @Column(nullable = false)
    private Boolean isDefault;
}
