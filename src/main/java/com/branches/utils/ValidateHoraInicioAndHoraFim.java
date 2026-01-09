package com.branches.utils;

import com.branches.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class ValidateHoraInicioAndHoraFim {
    public void execute(LocalTime horaInicio, LocalTime horaFim, Integer minutosIntervalo) {
        if (horaInicio == null && horaFim == null) return;

        if (horaInicio == null || horaFim == null) {
            throw new BadRequestException("Ambos os campos 'horaInicio' e 'horaFim' devem ser preenchidos ou ambos devem ser nulos");
        }

        if (horaInicio.isAfter(horaFim)) {
            throw new BadRequestException("O campo 'horaInicio' não pode ser maior que o campo 'horaFim'");
        }

        if (minutosIntervalo == null) return;

        if (Duration.between(horaInicio, horaFim).minus(Duration.ofMinutes(minutosIntervalo)).isNegative()) {
            throw new BadRequestException("O intervalo de tempo entre 'horaInicio' e 'horaFim' deve ser maior que o 'minutosIntervalo'");
        }
    }
}
