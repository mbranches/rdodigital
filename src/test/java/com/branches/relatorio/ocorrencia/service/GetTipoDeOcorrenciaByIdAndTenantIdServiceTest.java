package com.branches.relatorio.ocorrencia.service;

import com.branches.exception.NotFoundException;
import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.ocorrencia.repository.TipoDeOcorrenciaRepository;
import com.branches.ocorrencia.service.GetTipoDeOcorrenciaByIdAndTenantIdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTipoDeOcorrenciaByIdAndTenantIdServiceTest {

    @Mock
    private TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    @InjectMocks
    private GetTipoDeOcorrenciaByIdAndTenantIdService getTipoDeOcorrenciaByIdAndTenantIdService;

    private Long tipoDeOcorrenciaId;
    private Long tenantId;
    private TipoDeOcorrenciaEntity tipoDeOcorrenciaEntity;

    @BeforeEach
    void setUp() {
        tipoDeOcorrenciaId = 1L;
        tenantId = 1L;

        tipoDeOcorrenciaEntity = TipoDeOcorrenciaEntity.builder()
                .id(tipoDeOcorrenciaId)
                .descricao("Tipo de Ocorrência Teste")
                .ativo(true)
                .build();
        tipoDeOcorrenciaEntity.setTenantId(tenantId);
    }

    @Test
    void deveRetornarTipoDeOcorrenciaQuandoEncontrado() {
        when(tipoDeOcorrenciaRepository.findByIdAndTenantIdAndAtivoIsTrue(tipoDeOcorrenciaId, tenantId))
                .thenReturn(Optional.of(tipoDeOcorrenciaEntity));

        TipoDeOcorrenciaEntity result = getTipoDeOcorrenciaByIdAndTenantIdService.execute(tipoDeOcorrenciaId, tenantId);

        assertNotNull(result);
        assertEquals(tipoDeOcorrenciaId, result.getId());
        assertEquals("Tipo de Ocorrência Teste", result.getDescricao());
        assertEquals(tenantId, result.getTenantId());
        assertTrue(result.getAtivo());

        verify(tipoDeOcorrenciaRepository, times(1)).findByIdAndTenantIdAndAtivoIsTrue(tipoDeOcorrenciaId, tenantId);
    }

    @Test
    void deveLancarNotFoundExceptionQuandoTipoDeOcorrenciaNaoEncontrado() {
        when(tipoDeOcorrenciaRepository.findByIdAndTenantIdAndAtivoIsTrue(tipoDeOcorrenciaId, tenantId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getTipoDeOcorrenciaByIdAndTenantIdService.execute(tipoDeOcorrenciaId, tenantId)
        );

        assertNotNull(exception);
        assertEquals("Tipo de ocorrência não encontrado com o id: " + tipoDeOcorrenciaId,
                     exception.getReason());

        verify(tipoDeOcorrenciaRepository, times(1)).findByIdAndTenantIdAndAtivoIsTrue(tipoDeOcorrenciaId, tenantId);
    }
}

