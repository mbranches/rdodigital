package com.branches.assinaturadeplano.domain.enums;

import java.util.List;

public enum AssinaturaStatus {
    ATIVO,
    VENCIDO,
    ENCERRADO,
    SUSPENSO,
    CANCELADO,
    PENDENTE,
    NAO_FINALIZADO;

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
        return List.of(ATIVO, VENCIDO, SUSPENSO, PENDENTE);
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
