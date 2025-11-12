package com.branches.relatorio.maodeobra.service;

import com.branches.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@RequiredArgsConstructor
@Service
public class GetHorasTrabalhadaOfMaoDeObraService {
    public LocalTime execute(LocalTime horaInicio, LocalTime horaFim, LocalTime horasIntervalo) {
        if ((horaInicio != null && horaFim == null) || (horaInicio == null && horaFim != null)) {
            throw new BadRequestException("Quando hora de início ou hora de fim forem preenchidas, ambas devem ser preenchidas");
        }

        if (horaInicio == null) {
            return null;
        }

        int horasDeIntervalo = horasIntervalo != null ? horasIntervalo.getHour() : 0;
        int minutosDeIntervalo = horasIntervalo != null ? horasIntervalo.getMinute() : 0;

        return horaFim.minusHours(horaInicio.getHour()).minusMinutes(horaInicio.getMinute())
                .minusHours(horasDeIntervalo).minusMinutes(minutosDeIntervalo);
    }
}
