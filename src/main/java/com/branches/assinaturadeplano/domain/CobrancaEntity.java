package com.branches.assinaturadeplano.domain;

import com.branches.assinaturadeplano.domain.enums.StatusCobranca;
import com.branches.config.envers.AuditableTenantOwned;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CobrancaEntity extends AuditableTenantOwned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AssinaturaDePlanoEntity assinatura;

    private String stripeInvoiceId;

    private LocalDate inicioPeriodoCobranca;
    private LocalDate fimPeriodoCobranca;

    private BigDecimal valorCobranca;

    @Enumerated(EnumType.STRING)
    private StatusCobranca status;

    private LocalDate dataVencimento;
    private LocalDate dataPagamento;

    private String linkCobranca;
    private String linkPdfCobranca;

    public boolean isPaga() {
        return this.status == StatusCobranca.PAGA;
    }

    public void pagar(LocalDate dataPagamento) {
        this.status = StatusCobranca.PAGA;
        this.dataPagamento = dataPagamento;
    }

    public void updateLinks(String hostedInvoiceUrl, String invoicePdf) {
        this.linkCobranca = hostedInvoiceUrl;
        this.linkPdfCobranca = invoicePdf;
    }

    public void definirFalhaPagamento() {
        this.status = StatusCobranca.FALHA_PAGAMENTO;
    }
}