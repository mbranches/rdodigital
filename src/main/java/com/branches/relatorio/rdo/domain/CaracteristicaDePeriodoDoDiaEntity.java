package com.branches.relatorio.rdo.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.relatorio.rdo.domain.enums.Clima;
import com.branches.relatorio.rdo.domain.enums.CondicaoDoTempo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@With
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CaracteristicaDePeriodoDoDiaEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Clima clima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CondicaoDoTempo condicaoDoTempo;

    @Column(nullable = false)
    private Boolean ativo;
}