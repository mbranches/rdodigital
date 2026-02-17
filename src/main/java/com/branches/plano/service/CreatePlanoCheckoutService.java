package com.branches.plano.service;

import com.branches.assinaturadeplano.service.FindAssinaturaCorrenteByTenantIdService;
import com.branches.exception.BadRequestException;
import com.branches.external.stripe.CreateStripeCheckoutSession;
import com.branches.external.stripe.CreateStripeCheckoutSessionResponse;
import com.branches.external.stripe.CreateStripeCustomer;
import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.dto.request.CreatePlanoCheckoutRequest;
import com.branches.plano.dto.response.PlanoCheckoutResponse;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.TenantRepository;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.user.service.GetUserByIdService;
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
    private final TenantRepository tenantRepository;
    private final GetCurrentUserTenantService getCurrentUserTenantService;
    private final GetPlanoByIdService getPlanoByIdService;
    private final CreateStripeCheckoutSession createStripeCheckoutSession;
    private final FindAssinaturaCorrenteByTenantIdService findAssinaturaCorrenteByTenantIdService;
    private final GetTenantByIdExternoService getTenantByIdExternoService;
    private final CreateStripeCustomer createStripeCustomer;
    private final GetUserByIdService getUserByIdService;

    public PlanoCheckoutResponse execute(CreatePlanoCheckoutRequest request, String tenantExternalId, List<UserTenantEntity> userTenants) {
        log.info("Iniciando criação de checkout para o plano: {} e tenant: {}", request.planoId(), tenantExternalId);
        TenantEntity tenant = getTenantByIdExternoService.execute(tenantExternalId);

        Long tenantId = tenant.getId();
        getCurrentUserTenantService.execute(userTenants, tenantId);

        checkIfTenantCanCreateCheckout(tenantId);

        PlanoEntity plano = getPlanoByIdService.execute(request.planoId());

        UserEntity userResponsavel = getUserByIdService.execute(tenant.getUserResponsavelId());

        boolean tenantHasNotStripeId = tenant.getStripeCustomerId() == null;
        String stripeCustomerId = tenantHasNotStripeId ? createCustomerForTenant(tenant, userResponsavel)
                : tenant.getStripeCustomerId();

        CreateStripeCheckoutSessionResponse stripeResponse = createStripeCheckoutSession.execute(
                plano.getStripePriceId(),
                stripeCustomerId,
                tenantId
        );

        log.info("Checkout criado com sucesso para o tenant: {} e plano: {}", tenantId, plano.getNome());

        return new PlanoCheckoutResponse(stripeResponse.checkoutUrl());
    }

    private String createCustomerForTenant(TenantEntity tenant, UserEntity userResponsavel) {
        String stripeCustomerId = createStripeCustomer.execute(tenant.getRazaoSocial(), userResponsavel.getEmail());

        tenant.setStripeCustomerId(stripeCustomerId);

        tenantRepository.save(tenant);

        log.info("Cliente Stripe criado com sucesso para o tenant: {}. Stripe Customer ID: {}", tenant.getId(), stripeCustomerId);
        return stripeCustomerId;
    }

    private void checkIfTenantCanCreateCheckout(Long tenantId) {
        findAssinaturaCorrenteByTenantIdService.execute(tenantId).ifPresent(assinatura -> {
            throw new BadRequestException("Tenant já possui uma assinatura ativa e não pode criar um novo checkout, ainda não é possível alterar planos antes do término da assinatura atual.");
        });
    }
}
