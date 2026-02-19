package com.branches.external.stripe;

import com.branches.assinaturadeplano.domain.AssinaturaDePlanoEntity;
import com.branches.assinaturadeplano.domain.AssinaturaHistoricoEntity;
import com.branches.assinaturadeplano.domain.CobrancaEntity;
import com.branches.assinaturadeplano.domain.enums.EventoHistoricoAssinatura;
import com.branches.assinaturadeplano.domain.enums.StatusCobranca;
import com.branches.assinaturadeplano.repository.AssinaturaDePlanoRepository;
import com.branches.assinaturadeplano.repository.AssinaturaHistoricoRepository;
import com.branches.assinaturadeplano.repository.CobrancaRepository;
import com.branches.exception.NotFoundException;
import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.service.GetPlanoByStripeIdService;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Transactional
@Log4j2
@RequiredArgsConstructor
@Service
public class StripeEventsHandlerService {
    private final CobrancaRepository cobrancaRepository;
    private final GetPlanoByStripeIdService getPlanoByStripeIdService;
    private final AssinaturaHistoricoRepository assinaturaHistoricoRepository;
    private final AssinaturaDePlanoRepository assinaturaDePlanoRepository;
    private static final ZoneId TIMEZONE_SP = ZoneId.of("America/Sao_Paulo");

    public void handle(Event event) {
        switch (event.getType()) {
            case "invoice.paid" -> handleInvoicePaid(event);

            case "invoice.payment_failed" -> handleInvoicePaymentFailed(event);

            case "invoice.finalized" -> handleInvoiceFinalized(event);

            case "customer.subscription.updated" -> handleSubscriptionUpdated(event);

            case "customer.subscription.deleted" -> handleSubscriptionDeleted(event);

            default -> log.info("Evento Stripe não implementado: {}", event.getType());
        }
    }

    private String getPriceIdOfSubscription(Subscription subscription) {
        return subscription.getItems().getData().stream()
                .map(SubscriptionItem::getPrice)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Price da assinatura não encontrado. SubscriptionId={}", subscription.getId());
                    return new NotFoundException("Price da assinatura não encontrado para SubscriptionId=" + subscription.getId());
                })
                .getId();
    }

    private void handleInvoicePaid(Event event) {
        log.info("Processando evento de invoice.paid do Stripe");
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> {
                    log.error("Invoice não encontrada no evento de invoice.paid");
                    return new NotFoundException("Invoice não encontrada no evento pago");
                });

        log.info("Invoice paga: {}", invoice.getId());

        log.info("Buscando cobrança associada à invoice: {}", invoice.getId());
        CobrancaEntity cobranca = cobrancaRepository.findByStripeInvoiceId(invoice.getId())
                .orElse(createCobrancaFromInvoice(invoice));
        log.info("Cobrança encontrada para a invoice: {}. Status atual da cobrança: {}", invoice.getId(), cobranca.getStatus());

        if (cobranca.isPaga() && cobranca.getDataPagamento() != null) {
            log.info("Cobrança já paga para a invoice: {}", invoice.getId());

            cobrancaRepository.save(cobranca);

            return;
        }
        LocalDate dataPagamento = epochToLocalDate(
                invoice.getStatusTransitions().getPaidAt()
        );
        cobranca.pagar(dataPagamento);

        cobrancaRepository.save(cobranca);

        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

        AssinaturaDePlanoEntity assinatura = assinaturaDePlanoRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> {
                    log.error("Assinatura não encontrada para subscriptionId={}", subscriptionId);
                    return new NotFoundException("Assinatura não encontrada para subscriptionId=" + subscriptionId);
                });

        LocalDate dataFimCicloAtual = assinatura.getPlano().calcularDataFim(now());
        assinatura.ativar(dataFimCicloAtual);
        assinaturaDePlanoRepository.save(assinatura);

        log.info("Cobrança marcada como paga para a invoice: {}. Assinatura ativada: {}", invoice.getId(), assinatura.getId());
    }

    private CobrancaEntity createCobrancaFromInvoice(Invoice invoice) {
        BigDecimal valor = BigDecimal.valueOf(invoice.getAmountDue()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        LocalDate dataVencimento = invoice.getDueDate() != null ? epochToLocalDate(invoice.getDueDate())
                : null;

        LocalDate inicioPeriodoCobranca = epochToLocalDate(invoice.getPeriodStart());

        LocalDate fimPeriodoCobranca = epochToLocalDate(invoice.getPeriodEnd());

        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();
        AssinaturaDePlanoEntity assinatura = assinaturaDePlanoRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para subscriptionId=" + subscriptionId));


        CobrancaEntity cobranca = CobrancaEntity.builder()
                .tenantId(assinatura.getTenantId())
                .stripeInvoiceId(invoice.getId())
                .valorCobranca(valor)
                .dataVencimento(dataVencimento)
                .status(StatusCobranca.PENDENTE)
                .assinatura(assinatura)
                .inicioPeriodoCobranca(inicioPeriodoCobranca)
                .fimPeriodoCobranca(fimPeriodoCobranca)
                .linkCobranca(invoice.getHostedInvoiceUrl())
                .linkPdfCobranca(invoice.getInvoicePdf())
                .build();

        LocalDate paidAt = invoice.getStatusTransitions() != null && invoice.getStatusTransitions().getPaidAt() != null
                ? epochToLocalDate(invoice.getStatusTransitions().getPaidAt())
                : null;

        if (paidAt != null && "paid".equals(invoice.getStatus())) {
            cobranca.pagar(paidAt);
        }

        if (invoice.getStatus().equals("past_due") || invoice.getStatus().equals("uncollectible")) {
            cobranca.definirFalhaPagamento();
        }

        return cobranca;
    }

    private LocalDate now() {
        return Instant.now().atZone(TIMEZONE_SP).toLocalDate();
    }

    private LocalDate epochToLocalDate(Long epoch) {
        return epoch != null
                ? Instant.ofEpochSecond(epoch).atZone(TIMEZONE_SP).toLocalDate()
                : null;
    }

    private void handleInvoicePaymentFailed(Event event) {
        log.info("Processando evento de invoice.payment_failed do Stripe");
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> {
                    log.error("Invoice não encontrada no evento de invoice.payment_failed");
                    return new NotFoundException("Invoice não encontrada no evento de falha de pagamento");
                });

        log.info("Pagamento falhou para a invoice: {}", invoice.getId());

        CobrancaEntity cobranca = cobrancaRepository.findByStripeInvoiceId(invoice.getId())
                .orElse(createCobrancaFromInvoice(invoice));

        if(cobranca.isFalhaPagamento()) {
            log.info("Cobrança já marcada como falha de pagamento para a invoice: {}", invoice.getId());
            cobrancaRepository.save(cobranca);
            return;
        }

        cobranca.definirFalhaPagamento();

        cobrancaRepository.save(cobranca);

        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

        AssinaturaDePlanoEntity assinaturaDePlanoEntity = assinaturaDePlanoRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para subscriptionId=" + subscriptionId));

        if (assinaturaDePlanoEntity.isPendente()) {
            assinaturaDePlanoEntity.definirNaoFinalizada();

            assinaturaDePlanoRepository.save(assinaturaDePlanoEntity);
        }
    }

    private void handleInvoiceFinalized(Event event) {
        log.info("Processando evento de invoice.finalized do Stripe");
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> {
                    log.error("Invoice não encontrada no evento de invoice.finalized");
                    return new NotFoundException("Invoice não encontrada no evento finalizado");
                });

        CobrancaEntity cobranca = cobrancaRepository.findByStripeInvoiceId(invoice.getId())
                .orElse(createCobrancaFromInvoice(invoice));

        cobranca.updateLinks(invoice.getHostedInvoiceUrl(), invoice.getInvoicePdf());

        cobrancaRepository.save(cobranca);
    }

    private void registrarEventoAssinatura(AssinaturaDePlanoEntity assinatura, EventoHistoricoAssinatura evento) {
        AssinaturaHistoricoEntity historico = new AssinaturaHistoricoEntity(assinatura.getTenantId());
        historico.registrarEvento(assinatura, evento);
        assinaturaHistoricoRepository.save(historico);
    }

    private void handleSubscriptionUpdated(Event event) {
        log.info("Processando evento de customer.subscription.updated do Stripe");
        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> {
                    log.error("Subscription não encontrada no evento de customer.subscription.updated");
                    return new NotFoundException("Subscription não encontrada no evento atualizado");
                });

        String subscriptionId = subscription.getId();
        log.info("Atualizando assinatura Stripe: {}", subscriptionId);

        AssinaturaDePlanoEntity assinatura = assinaturaDePlanoRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para subscriptionId=" + subscriptionId));

        String planoPriceId = getPriceIdOfSubscription(subscription);

        PlanoEntity stripeCurrentPlano = getPlanoByStripeIdService.execute(planoPriceId);
        PlanoEntity assinaturaCurrentPlano = assinatura.getPlano();

        if (!stripeCurrentPlano.getId().equals(assinaturaCurrentPlano.getId())) {
            log.info("Plano da assinatura Stripe mudou. Atualizando plano da assinatura local. Assinatura Stripe: {}, Plano Stripe: {}, Plano Local: {}",
                    subscriptionId, stripeCurrentPlano.getNome(), assinaturaCurrentPlano.getNome());

            assinatura.desmarcarProcessamentoAtualizacaoPlano();

            assinatura.atualizarPlano(stripeCurrentPlano);

            boolean isUpgrade = stripeCurrentPlano.getValor().compareTo(assinaturaCurrentPlano.getValor()) > 0;

            EventoHistoricoAssinatura evento = isUpgrade ? EventoHistoricoAssinatura.UPGRADE : EventoHistoricoAssinatura.DOWNGRADE;

            registrarEventoAssinatura(assinatura, evento);
        }

        String stripeStatus = subscription.getStatus();

        processSubscriptionForStatus(assinatura, stripeStatus);

        assinaturaDePlanoRepository.save(assinatura);
    }

    private void processSubscriptionForStatus(AssinaturaDePlanoEntity assinatura, String stripeStatus) {
        switch (stripeStatus) {
            case "active" -> {
                LocalDate dataFimCicloAtual = assinatura.getPlano().calcularDataFim(now());

                assinatura.ativar(dataFimCicloAtual);
            }

            case "past_due" -> assinatura.definirVencido();

            case "unpaid" -> assinatura.definirSuspensa();

            case "canceled" -> {
                assinatura.cancelar();
                registrarEventoAssinatura(assinatura, EventoHistoricoAssinatura.CANCELAMENTO);
            }

            default -> log.info("Status Stripe não tratado: {}", stripeStatus);
        }
    }

    private void handleSubscriptionDeleted(Event event) {
        log.info("Processando evento de customer.subscription.deleted do Stripe");
        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> {
                    log.error("Subscription não encontrada no evento de customer.subscription.deleted");
                    return new NotFoundException("Subscription não encontrada no evento deletado");
                });

        String subscriptionId = subscription.getId();
        log.info("Recebido evento de encerramento definitivo (deleted) para Assinatura Stripe: {}", subscriptionId);

        AssinaturaDePlanoEntity assinatura = assinaturaDePlanoRepository.findByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada para subscriptionId=" + subscriptionId));

        if (!assinatura.isCancelada()) {
            log.info("Encerrando assinatura localmente após término do ciclo no Stripe.");
            assinatura.cancelar();

            assinaturaDePlanoRepository.save(assinatura);

            registrarEventoAssinatura(assinatura, EventoHistoricoAssinatura.CANCELAMENTO);
        }
    }
}
