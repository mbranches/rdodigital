package com.branches.shared.calculators;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class CalculatePrazoDecorrido {
    public BigDecimal execute(LocalDate dataInicio, LocalDate dataFim, LocalDate dataParaComparar) {
        long prazoTotal = ChronoUnit.DAYS.between(dataInicio, dataFim);
        long prazoDecorrido = ChronoUnit.DAYS.between(dataInicio, dataParaComparar);

        if (prazoTotal == 0 || prazoDecorrido >= prazoTotal) {
            return BigDecimal.valueOf(100);
        }

        if (prazoDecorrido <= 0) {
            return BigDecimal.valueOf(0);
        }

        return BigDecimal.valueOf(prazoDecorrido)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(prazoTotal), 2, RoundingMode.HALF_UP);
    }
}
