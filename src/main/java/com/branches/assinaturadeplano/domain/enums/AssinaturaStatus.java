package com.branches.assinaturadeplano.domain.enums;

import java.util.List;

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

    /**
     * Lista de status que indicam que a assinatura já teve um plano ativo em algum momento
     * @return Lista de status
     */
    public static List<AssinaturaStatus> getStatusListThatAlreadyHaveActivePlan(){
        return List.of(ATIVO, VENCIDO, ENCERRADO, SUSPENSO, CANCELADO);
    }

    /**
     * Lista de status que indicam que a assinatura está corrente (ativa, vencida ou suspensa)
     * São os status que indicam que a assinatura ainda pode ser reativada ou está em uso
     * @return Lista de status
     */
    public static List<AssinaturaStatus> getStatusListOfAssinaturaCorrente() {
        return List.of(ATIVO, VENCIDO, SUSPENSO);
    }

    /**
     * Lista de status que indicam que a assinatura está ativa ou vencida
     * São os status que ainda possuem acesso ao serviço
     * @return Lista de status
     */
    public static List<AssinaturaStatus> getStatusListOfAssinaturaAtiva() {
        return List.of(ATIVO, VENCIDO);
    }
}
