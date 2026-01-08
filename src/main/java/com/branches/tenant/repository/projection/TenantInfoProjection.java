package com.branches.tenant.repository.projection;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.plano.domain.PeriodoTesteEntity;
import com.branches.user.domain.UserEntity;

public interface TenantInfoProjection {
    String getIdExterno();
    String getRazaoSocial();
    String getNome();
    String getCnpj();
    String getTelefone();
    String getLogoUrl();
    UserEntity getResponsavel();
    AssinaturaEntity getAssinaturaCorrente();
    Long getQuantidadeDeUsersCriados();
    Long getQuantidadeDeObrasCriadas();
    Long getQuantidadeDeRelatoriosCriados();
    PeriodoTesteEntity getPeriodoDeTeste();
    Boolean getAlreadyHadSubscription();
}
