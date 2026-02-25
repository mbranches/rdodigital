package com.branches.plano.domain.enums;

import com.stripe.param.PriceCreateParams;

public enum RecorrenciaPlano {
//    DIARIO,
    MENSAL;
//    MENSAL_AVULSO,
//    SEMANAL,
//    ANUAL;

    public PriceCreateParams.Recurring.Interval toInterval() {
        return switch (this) {
//            case DIARIO -> com.stripe.param.PriceCreateParams.Recurring.Interval.DAY;
//            case SEMANAL -> com.stripe.param.PriceCreateParams.Recurring.Interval.WEEK;
            case MENSAL -> PriceCreateParams.Recurring.Interval.MONTH;
//            case ANUAL -> com.stripe.param.PriceCreateParams.Recurring.Interval.YEAR;
//            case MENSAL_AVULSO -> null;
        };
    }
}
