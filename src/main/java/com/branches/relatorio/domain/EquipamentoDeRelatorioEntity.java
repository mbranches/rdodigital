package com.branches.relatorio.domain;

import com.branches.equipamento.domain.EquipamentoEntity;
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
