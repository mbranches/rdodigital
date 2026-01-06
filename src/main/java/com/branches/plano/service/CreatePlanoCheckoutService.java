package com.branches.plano.service;

import com.branches.external.stripe.CreateStripeCheckoutSession;
import com.branches.external.stripe.CreateStripeCheckoutSessionResponse;
import com.branches.plano.domain.IntencaoDePagamentoEntity;
import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.dto.request.CreatePlanoCheckoutRequest;
import com.branches.plano.dto.response.PlanoCheckoutResponse;
import com.branches.plano.repository.IntencaoDePagamentoRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class CreatePlanoCheckoutService {
    private final IntencaoDePagamentoRepository intencaoDePagamentoRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetPlanoByIdService getPlanoByIdService;
    private final CreateStripeCheckoutSession createStripeCheckoutSession;

    public PlanoCheckoutResponse execute(CreatePlanoCheckoutRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        log.info("Iniciando criação de checkout para o plano: {} e tenant: {}", request.planoId(), tenantExternalId);
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        getCurrentUserTenantService.execute(userTenants, tenantId);

        PlanoEntity plano = getPlanoByIdService.execute(request.planoId());

        CreateStripeCheckoutSessionResponse stripeResponse = createStripeCheckoutSession.execute(
                plano.getStripePriceId()
        );

        IntencaoDePagamentoEntity intencao = IntencaoDePagamentoEntity.builder()
                .tenantId(tenantId)
                .planoId(plano.getId())
                .stripeSessionId(stripeResponse.sessionId())
                .build();

        intencaoDePagamentoRepository.save(intencao);

        log.info("Checkout criado com sucesso para o tenant: {} e plano: {}", tenantId, plano.getNome());

        return new PlanoCheckoutResponse(stripeResponse.checkoutUrl());
    }
}
