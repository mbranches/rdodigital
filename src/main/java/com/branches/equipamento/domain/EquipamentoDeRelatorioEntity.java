package com.branches.equipamento.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.relatorio.domain.RelatorioEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EquipamentoDeRelatorioEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "equipamento_id", nullable = false)
    private EquipamentoEntity equipamento;
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
    private Integer quantidade;
}
