package com.branches.plano.domain;

import com.branches.config.envers.AuditableTenantOwned;
import com.branches.plano.domain.enums.StatusIntencaoDePagamento;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class IntencaoDePagamentoEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long planoId;

    @Column(nullable = false)
    private String stripeSessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusIntencaoDePagamento status = StatusIntencaoDePagamento.PENDENTE;

    public void concluir() {
        this.status = StatusIntencaoDePagamento.CONCLUIDO;
    }
}
