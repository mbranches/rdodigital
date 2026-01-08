package com.branches.external.stripe;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.domain.enums.AssinaturaStatus;
import com.branches.assinatura.repository.AssinaturaRepository;
import com.branches.exception.NotFoundException;
import com.branches.plano.domain.IntencaoDePagamentoEntity;
import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.domain.enums.RecorrenciaPlano;
import com.branches.plano.repository.IntencaoDePagamentoRepository;
import com.branches.plano.repository.PlanoRepository;
import com.branches.plano.service.FinalizarPeriodoDeTesteIfToExistService;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class StripeSubscriptionService {
    private final IntencaoDePagamentoRepository intencaoDePagamentoRepository;
    private final PlanoRepository planoRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final FinalizarPeriodoDeTesteIfToExistService finalizarPeriodoDeTesteIfToExistService;

    public void register(String sessionId, Subscription subscription) {
        log.info("Criando assinatura para a sessão: {}", sessionId);
        String subscriptionId = subscription != null ? subscription.getId() : null;

        IntencaoDePagamentoEntity intencaoDePagamentoEntity = intencaoDePagamentoRepository.findByStripeSessionId(sessionId)
                .orElseThrow(() -> new NotFoundException("Intenção de pagamento não encontrada para a sessão: " + sessionId));

        PlanoEntity plano = planoRepository.findById(intencaoDePagamentoEntity.getPlanoId())
                .orElseThrow(() -> new NotFoundException("Plano não encontrado para o ID: " + intencaoDePagamentoEntity.getPlanoId()));

        RecorrenciaPlano recorrenciaPlano = plano.getRecorrencia();
        if (recorrenciaPlano != RecorrenciaPlano.MENSAL_AVULSO && subscription == null) {
            throw new NotFoundException("Assinatura do Stripe não encontrada para o plano recorrente na sessão: " + sessionId);
        }

        intencaoDePagamentoEntity.concluir();

        LocalDate dataFim = recorrenciaPlano != RecorrenciaPlano.MENSAL_AVULSO ? Instant.ofEpochSecond(subscription.getBillingCycleAnchor())
                .atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate() : LocalDate.now().plusMonths(1);

        Long tenantId = intencaoDePagamentoEntity.getTenantId();
        AssinaturaEntity assinatura = AssinaturaEntity.builder()
                .status(AssinaturaStatus.PENDENTE)
                .stripeSubscriptionId(subscriptionId)
                .plano(plano)
                .dataInicio(LocalDate.now())
                .dataFim(dataFim)
                .intencaoDePagamento(intencaoDePagamentoEntity)
                .tenantId(tenantId)
                .build();

        if(recorrenciaPlano == RecorrenciaPlano.MENSAL_AVULSO) {
            assinatura.ativar();

            finalizarPeriodoDeTesteIfToExistService.execute(tenantId);
        }

        assinaturaRepository.save(assinatura);

        log.info("Assinatura criada com sucesso para a sessão: {}", sessionId);
    }

    public void cancel(String subscriptionId) {
        log.info("Cancelando assinatura com ID do Stripe: {}", subscriptionId);

        AssinaturaEntity assinatura = assinaturaRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para o ID da assinatura do Stripe: " + subscriptionId));

        assinatura.cancelar();

        assinaturaRepository.save(assinatura);

        log.info("Assinatura cancelada com sucesso para o ID do Stripe: {}", subscriptionId);
    }

    public void update(Subscription subscription) {
        log.info("Atualizando assinatura com ID do Stripe: {}", subscription.getId());

        String subscriptionId = subscription.getId();
        String stripeStatus = subscription.getStatus();

        AssinaturaEntity assinatura = assinaturaRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para o ID da assinatura do Stripe: " + subscriptionId));

        AssinaturaStatus newStatus = AssinaturaStatus.fromStripeStatus(stripeStatus);
        log.info("Status atual da assinatura: {}, Novo status: {}", assinatura.getStatus(), newStatus);

        if (newStatus != assinatura.getStatus()) {
            assinatura.setStatus(newStatus);

            if (newStatus == AssinaturaStatus.ATIVO && assinatura.getPlano() != null) {
                assinatura.setDataFim(
                        Instant.ofEpochSecond(subscription.getBillingCycleAnchor())
                                .atZone(ZoneId.of("America/Sao_Paulo"))
                                .toLocalDate()
                );
            }

            assinaturaRepository.save(assinatura);

            log.info("Assinatura atualizada com sucesso para o ID do Stripe: {}", subscriptionId);
        }
    }

    public void handlePaymentFailed(Subscription subscription) {
        log.info("Processando falha de pagamento para assinatura com ID do Stripe: {}", subscription.getId());
        String subscriptionId = subscription.getId();

        AssinaturaEntity assinatura = assinaturaRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para o ID da assinatura do Stripe: " + subscriptionId));

        assinatura.setStatus(AssinaturaStatus.VENCIDO);

        assinaturaRepository.save(assinatura);
        log.info("Assinatura marcada como VENCIDO para o ID do Stripe: {}", subscriptionId);
    }

    public void handlePaymentSucceeded(Subscription subscription) {
        log.info("Processando pagamento bem-sucedido para assinatura com ID do Stripe: {}", subscription.getId());
        String subscriptionId = subscription.getId();

        AssinaturaEntity assinatura = assinaturaRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para o ID da assinatura do Stripe: " + subscriptionId));

        assinatura.ativar();
        finalizarPeriodoDeTesteIfToExistService.execute(assinatura.getTenantId());

        assinaturaRepository.save(assinatura);
        log.info("Assinatura marcada como ATIVO para o ID do Stripe: {}", subscriptionId);
    }
}
