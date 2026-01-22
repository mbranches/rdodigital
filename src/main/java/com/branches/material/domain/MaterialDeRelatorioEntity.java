package com.branches.material.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.relatorio.domain.RelatorioEntity;
import com.branches.material.domain.enums.TipoMaterial;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
public class MaterialDeRelatorioEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private MaterialEntity material;

    private String quantidade;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMaterial tipoMaterial;

    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
}
