package com.branches.equipamento.domain;

import com.branches.relatorio.domain.RelatorioEntity;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EquipamentoDeRelatorioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "equipamento_id", nullable = false)
    private EquipamentoEntity equipamento;
    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;
    @Column(nullable = false)
    private Integer quantidade;
}
