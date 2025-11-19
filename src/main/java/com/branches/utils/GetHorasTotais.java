package com.branches.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@RequiredArgsConstructor
@Service
public class GetHorasTotais {
    private final ValidateHoraInicioAndHoraFim validateHoraInicioAndHoraFim;

    public LocalTime execute(LocalTime horaInicio, LocalTime horaFim, LocalTime horasIntervalo) {
        validateHoraInicioAndHoraFim.execute(horaInicio, horaFim);

        int horasDeIntervalo = horasIntervalo != null ? horasIntervalo.getHour() : 0;
        int minutosDeIntervalo = horasIntervalo != null ? horasIntervalo.getMinute() : 0;

        return horaFim.minusHours(horaInicio.getHour()).minusMinutes(horaInicio.getMinute())
                .minusHours(horasDeIntervalo).minusMinutes(minutosDeIntervalo);
    }
}
