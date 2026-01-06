package com.branches.assinatura.domain.enums;

public enum AssinaturaStatus {
    ATIVO,
    VENCIDO,
    ENCERRADO,
    SUSPENSO,
    CANCELADO,
    NAO_INICIADO,
    PENDENTE,
    INCOMPLETO;

    public static AssinaturaStatus fromStripeStatus(String status) {
        return switch (status) {
            case "trialing" -> throw new IllegalArgumentException("Não implementado o status trialing"); //todo: implementar periodo de trial
            case "active" -> ATIVO;
            case "incomplete" -> INCOMPLETO;
            case "incomplete_expired" -> NAO_INICIADO;
            case "past_due" -> VENCIDO;
            case "canceled" -> CANCELADO;
            case "unpaid", "paused" -> SUSPENSO;
            default -> throw new IllegalArgumentException("Status de assinatura desconhecido: " + status);
        };
    }
}
