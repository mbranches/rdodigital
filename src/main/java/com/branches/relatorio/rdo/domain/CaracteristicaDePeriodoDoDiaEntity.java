package com.branches.relatorio.rdo.domain;

import com.branches.relatorio.rdo.domain.enums.Clima;
import com.branches.relatorio.rdo.domain.enums.CondicaoDoTempo;
import jakarta.persistence.*;
import lombok.*;

@With
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CaracteristicaDePeriodoDoDiaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioEntity relatorio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Clima clima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CondicaoDoTempo condicaoDoTempo;

    @Column(nullable = false)
    private Boolean ativo;
}