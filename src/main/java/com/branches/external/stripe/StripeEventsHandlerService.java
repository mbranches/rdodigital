package com.branches.external.stripe;

import com.branches.assinaturadeplano.domain.AssinaturaDePlanoEntity;
import com.branches.assinaturadeplano.domain.AssinaturaHistoricoEntity;
import com.branches.assinaturadeplano.domain.CobrancaEntity;
import com.branches.assinaturadeplano.domain.enums.AssinaturaStatus;
import com.branches.assinaturadeplano.domain.enums.EventoHistoricoAssinatura;
import com.branches.assinaturadeplano.domain.enums.StatusCobranca;
import com.branches.assinaturadeplano.repository.AssinaturaDePlanoRepository;
import com.branches.assinaturadeplano.repository.AssinaturaHistoricoRepository;
import com.branches.assinaturadeplano.repository.CobrancaRepository;
import com.branches.assinaturadeplano.service.ExistsCobrancaByStripeIdService;
import com.branches.assinaturadeplano.service.GetAssinaturaByStripeIdService;
import com.branches.assinaturadeplano.service.GetCobrancaByStripeIdService;
import com.branches.exception.NotFoundException;
import com.branches.plano.domain.PlanoEntity;
import com.branches.plano.service.GetPlanoByStripeIdService;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.service.GetTenantByIdService;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
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
    private final ExistsCobrancaByStripeIdService existsCobrancaByStripeIdService;
    private final GetAssinaturaByStripeIdService getAssinaturaByStripeIdService;
    private final CobrancaRepository cobrancaRepository;
    private final GetCobrancaByStripeIdService getCobrancaByStripeIdService;
    private final GetPlanoByStripeIdService getPlanoByStripeIdService;
    private final AssinaturaHistoricoRepository assinaturaHistoricoRepository;
    private final AssinaturaDePlanoRepository assinaturaDePlanoRepository;
    private final GetTenantByIdService getTenantByIdService;

    public void handle(Event event) {
        switch (event.getType()) {
            case "invoice.created" -> handleInvoiceCreated(event);

            case "invoice.paid" -> handleInvoicePaid(event);

            case "invoice.payment_failed" -> handleInvoicePaymentFailed(event);

            case "invoice.finalized" -> handleInvoiceFinalized(event);

            case "checkout.session.completed" -> handleCheckoutSessionCompleted(event);

            case "customer.subscription.updated" -> handleSubscriptionUpdated(event);

            case "customer.subscription.deleted" -> handleSubscriptionDeleted(event);

            default -> log.info("Evento Stripe não implementado: {}", event.getType());
        }
    }

    private void handleInvoiceCreated(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow();

        if (existsCobrancaByStripeIdService.execute(invoice.getId())) {
            log.error("Cobranca já existe para a invoice: {}", invoice.getId());

            return;
        }

        log.info("Invoice criada: {}", invoice.getId());

        BigDecimal valor = BigDecimal.valueOf(invoice.getAmountDue()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        LocalDate dataVencimento = invoice.getDueDate() != null ? Instant.ofEpochSecond(invoice.getDueDate())
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate() : null;

        LocalDate inicioPeriodoCobranca = Instant.ofEpochSecond(invoice.getPeriodStart())
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate();

        LocalDate fimPeriodoCobranca = Instant.ofEpochSecond(invoice.getPeriodEnd())
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate();

        AssinaturaDePlanoEntity assinatura = getAssinaturaByStripeIdService.execute(invoice.getSubscription());

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

        cobrancaRepository.save(cobranca);
    }

    private void handleInvoicePaid(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow();

        log.info("Invoice paga: {}", invoice.getId());

        CobrancaEntity cobranca = getCobrancaByStripeIdService.execute(invoice.getId());

        if (cobranca.isPaga()) {
            log.info("Cobrança já paga para a invoice: {}", invoice.getId());
            return;
        }
        LocalDate dataPagamento = Instant.ofEpochSecond(
                invoice.getStatusTransitions().getPaidAt()
        ).atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();
        cobranca.pagar(dataPagamento);

        cobrancaRepository.save(cobranca);
    }

    private void handleInvoicePaymentFailed(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow();

        log.info("Pagamento falhou para a invoice: {}", invoice.getId());



        CobrancaEntity cobranca = getCobrancaByStripeIdService.execute(invoice.getId());

        cobranca.definirFalhaPagamento();

        cobrancaRepository.save(cobranca);
    }

    private void handleInvoiceFinalized(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow();

        CobrancaEntity cobranca = getCobrancaByStripeIdService.execute(invoice.getId());

        cobranca.updateLinks(invoice.getHostedInvoiceUrl(), invoice.getInvoicePdf());

        cobrancaRepository.save(cobranca);
    }

    private void handleCheckoutSessionCompleted(Event event) {
        Session session;
        try {
            session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
        } catch (Exception e) {
            log.error("Erro ao desserializar checkout.session.completed: {}", e.getMessage(), e);
            throw new NotFoundException("Erro ao processar evento de checkout session completed");
        }

        log.info("Checkout session completed: {}", session.getId());

        String tenantIdStr = session.getMetadata() != null ? session.getMetadata().get("tenantId") : null;

        if (tenantIdStr == null) {
            log.warn("Checkout session não contém tenantId no metadata. SessionId={}", session.getId());
            return;
        }

        String subscriptionId = session.getSubscription();

        if (subscriptionId == null) {
            log.warn("Checkout session não contém subscription. SessionId={}", session.getId());
            return;
        }

        TenantEntity tenant = getTenantByIdService.execute(Long.valueOf(tenantIdStr));

        boolean assinaturaExiste = assinaturaDePlanoRepository
                .existsByStripeSubscriptionId(subscriptionId);

        if (assinaturaExiste) {
            log.info("Assinatura já existe para subscriptionId={}", subscriptionId);
            return;
        }

        Subscription subscription;
        try {
            subscription = Subscription.retrieve(subscriptionId);
        } catch (Exception e) {
            log.error("Erro ao buscar subscription no Stripe. SubscriptionId={}. Erro: {}", subscriptionId, e.getMessage());
            throw new NotFoundException("Erro ao buscar subscription no Stripe. SubscriptionId=" + subscriptionId);
        }

        String priceId = subscription.getItems().getData().stream()
                .map(SubscriptionItem::getPrice)
                .findFirst()
                .orElseThrow()
                .getId();

        PlanoEntity plano = getPlanoByStripeIdService.execute(priceId);

        LocalDate dataInicio = Instant.ofEpochSecond(subscription.getCurrentPeriodStart())
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate();

        LocalDate dataFim = Instant.ofEpochSecond(subscription.getCurrentPeriodEnd())
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toLocalDate();

        AssinaturaDePlanoEntity assinatura = AssinaturaDePlanoEntity.builder()
                .tenantId(tenant.getId())
                .plano(plano)
                .stripeSubscriptionId(subscriptionId)
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .status(AssinaturaStatus.PENDENTE)
                .build();

        assinaturaDePlanoRepository.save(assinatura);

        AssinaturaHistoricoEntity historico = new AssinaturaHistoricoEntity(tenant.getId());
        historico.registrarEvento(assinatura, EventoHistoricoAssinatura.CRIACAO);

        assinaturaHistoricoRepository.save(historico);

        log.info("Assinatura criada com sucesso via checkout.session.completed. SubscriptionId={}", subscriptionId);
    }
    private void handleSubscriptionUpdated(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow();

        log.info("Atualizando assinatura Stripe: {}", subscription.getId());

        AssinaturaDePlanoEntity assinatura = getAssinaturaByStripeIdService.execute(subscription.getId());

        String planoPriceId = subscription.getItems().getData().stream()
                .map(SubscriptionItem::getPrice)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Price da assinatura não encontrado"))
                .getId();

        PlanoEntity stripeCurrentPlano = getPlanoByStripeIdService.execute(planoPriceId);
        PlanoEntity assinaturaCurrentPlano = assinatura.getPlano();

        if (!stripeCurrentPlano.getId().equals(assinaturaCurrentPlano.getId())) {
            log.info("Plano da assinatura Stripe mudou. Atualizando plano da assinatura local. Assinatura Stripe: {}, Plano Stripe: {}, Plano Local: {}",
                    subscription.getId(), stripeCurrentPlano.getNome(), assinaturaCurrentPlano.getNome());

            assinatura.desmarcarProcessamentoAtualizacaoPlano();

            assinatura.atualizarPlano(stripeCurrentPlano);

            AssinaturaHistoricoEntity assinaturaHistorico = new AssinaturaHistoricoEntity(assinatura.getTenantId());
            boolean isUpgrade = stripeCurrentPlano.getValor().compareTo(assinaturaCurrentPlano.getValor()) > 0;

            EventoHistoricoAssinatura evento = isUpgrade ? EventoHistoricoAssinatura.UPGRADE : EventoHistoricoAssinatura.DOWNGRADE;

            assinaturaHistorico.registrarEvento(assinatura, evento);

            assinaturaHistoricoRepository.save(assinaturaHistorico);
        }

        String stripeStatus = subscription.getStatus();

        switch (stripeStatus) {
            case "active" -> {
                LocalDate dataFimAssinatura = Instant.ofEpochSecond(subscription.getCurrentPeriodEnd()).atZone(ZoneId.of("America/Sao_Paulo")).toLocalDate();

                assinatura.ativar(dataFimAssinatura);
            }

            case "past_due" -> assinatura.definirVencido();

            case "unpaid" -> assinatura.definirSuspensa();

            case "canceled" -> {
                log.info("Cancelando assinatura Stripe: {}", subscription.getId());
                assinatura.cancelar();

                log.info("Registrando evento de cancelamento para a assinatura Stripe: {}", subscription.getId());
                AssinaturaHistoricoEntity historico = new AssinaturaHistoricoEntity(assinatura.getTenantId());
                historico.registrarEvento(assinatura, EventoHistoricoAssinatura.CANCELAMENTO);

                assinaturaHistoricoRepository.save(historico);
            }

            default -> log.info("Status Stripe não tratado: {}", stripeStatus);
        }

        assinaturaDePlanoRepository.save(assinatura);
    }

    private void handleSubscriptionDeleted(Event event) {
        Subscription subscription = (Subscription) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow();

        log.info("Recebido evento de encerramento definitivo (deleted) para Assinatura Stripe: {}", subscription.getId());

        AssinaturaDePlanoEntity assinatura = getAssinaturaByStripeIdService.execute(subscription.getId());

        if (!assinatura.isCancelada()) {
            log.info("Encerrando assinatura localmente após término do ciclo no Stripe.");
            assinatura.cancelar();

            AssinaturaHistoricoEntity historico = new AssinaturaHistoricoEntity();
            historico.registrarEvento(assinatura, EventoHistoricoAssinatura.CANCELAMENTO);
            assinaturaHistoricoRepository.save(historico);

            assinaturaDePlanoRepository.save(assinatura);
        }
    }
}
