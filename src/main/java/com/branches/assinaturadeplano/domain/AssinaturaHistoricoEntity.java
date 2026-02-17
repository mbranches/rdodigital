package com.branches.assinaturadeplano.domain;

import com.branches.assinaturadeplano.domain.enums.EventoHistoricoAssinatura;
import com.branches.config.envers.AuditableTenantOwned;
import com.branches.plano.domain.PlanoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AssinaturaHistoricoEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AssinaturaDePlanoEntity assinatura;

    @ManyToOne
    private PlanoEntity plano;

    @Enumerated(EnumType.STRING)
    private EventoHistoricoAssinatura evento;

    private BigDecimal valorMomentoEvento;

    public AssinaturaHistoricoEntity(Long tenantId) {
        super.setTenantId(tenantId);
    }


    public void registrarEvento(AssinaturaDePlanoEntity assinatura, EventoHistoricoAssinatura eventoHistoricoAssinatura) {
        this.assinatura = assinatura;
        this.plano = assinatura.getPlano();
        this.evento = eventoHistoricoAssinatura;
        this.valorMomentoEvento = assinatura.getPlano().getValor();
    }
}
