package com.branches.plano.domain.enums;

import com.stripe.param.PriceCreateParams;

public enum RecorrenciaPlano {
    DIARIO,
    MENSAL,
    SEMANAL,
    ANUAL;

    public PriceCreateParams.Recurring.Interval toInterval() {
        return switch (this) {
            case DIARIO -> PriceCreateParams.Recurring.Interval.DAY;
            case SEMANAL -> PriceCreateParams.Recurring.Interval.WEEK;
            case MENSAL -> PriceCreateParams.Recurring.Interval.MONTH;
            case ANUAL -> PriceCreateParams.Recurring.Interval.YEAR;
        };
    }
}
