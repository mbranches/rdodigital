package com.branches.relatorio.maodeobra.service;

import com.branches.exception.BadRequestException;
import com.branches.utils.CalculateHorasTotais;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalculateHorasTotaisTest {

    @InjectMocks
    private CalculateHorasTotais calculateHorasTotais;

    private LocalTime horaInicio;
    private LocalTime horaFim;
    private LocalTime horasIntervalo;

    @BeforeEach
    void setUp() {
        horaInicio = LocalTime.of(8, 0);
        horaFim = LocalTime.of(17, 0);
        horasIntervalo = LocalTime.of(1, 0);
    }

    @Test
    void deveCalcularHorasTrabalhadasCorretamente() {
        LocalTime result = calculateHorasTotais.execute(horaInicio, horaFim, horasIntervalo);

        assertNotNull(result);
        assertEquals(LocalTime.of(8, 0), result);
    }

    @Test
    void deveCalcularHorasTrabalhadasSemIntervalo() {
        LocalTime result = calculateHorasTotais.execute(horaInicio, horaFim, null);

        assertNotNull(result);
        assertEquals(LocalTime.of(9, 0), result);
    }

    @Test
    void deveRetornarNullQuandoHoraInicioEHoraFimForemNull() {
        LocalTime result = calculateHorasTotais.execute(null, null, null);

        assertNull(result);
    }

    @Test
    void deveLancarBadRequestExceptionQuandoApenasHoraInicioForPreenchida() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> calculateHorasTotais.execute(horaInicio, null, horasIntervalo)
        );

        assertNotNull(exception);
        assertEquals("Quando hora de início ou hora de fim forem preenchidas, ambas devem ser preenchidas",
                     exception.getReason());
    }

    @Test
    void deveLancarBadRequestExceptionQuandoApenasHoraFimForPreenchida() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> calculateHorasTotais.execute(null, horaFim, horasIntervalo)
        );

        assertNotNull(exception);
        assertEquals("Quando hora de início ou hora de fim forem preenchidas, ambas devem ser preenchidas",
                     exception.getReason());
    }

}