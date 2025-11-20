
package com.branches.obra.service;

import com.branches.exception.NotFoundException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.repository.ObraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetObraByIdExternoAndTenantIdServiceTest {

    @Mock
    private ObraRepository obraRepository;

    @InjectMocks
    private GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;

    private String obraIdExterno;
    private Long tenantId;
    private ObraEntity obraEntity;

    @BeforeEach
    void setUp() {
        obraIdExterno = "obra-ext-123";
        tenantId = 1L;

        obraEntity = ObraEntity.builder()
                .id(1L)
                .idExterno(obraIdExterno)
                .nome("Obra Teste")
                .responsavel("João Silva")
                .contratante("Contratante Teste")
                .tipoContrato(TipoContratoDeObra.CONTRATADA)
                .dataInicio(LocalDate.of(2025, 1, 1))
                .dataPrevistaFim(LocalDate.of(2025, 12, 31))
                .numeroContrato("CONT-2025-001")
                .endereco("Rua Teste, 123")
                .observacoes("Observações de teste")
                .tipoMaoDeObra(TipoMaoDeObra.PERSONALIZADA)
                .status(StatusObra.EM_ANDAMENTO)
                .ativo(true)
                .build();
        obraEntity.setTenantId(tenantId);
    }

    @Test
    void deveRetornarObraQuandoEncontrada() {
        when(obraRepository.findByIdExternoAndTenantIdAndAtivoIsTrue(obraIdExterno, tenantId))
                .thenReturn(Optional.of(obraEntity));

        ObraEntity result = getObraByIdExternoAndTenantIdService.execute(obraIdExterno, tenantId);

        assertNotNull(result);
        assertEquals(obraIdExterno, result.getIdExterno());
        assertEquals("Obra Teste", result.getNome());
        assertEquals(tenantId, result.getTenantId());
        assertEquals("João Silva", result.getResponsavel());
        assertEquals(StatusObra.EM_ANDAMENTO, result.getStatus());

        verify(obraRepository, times(1)).findByIdExternoAndTenantIdAndAtivoIsTrue(obraIdExterno, tenantId);
    }

    @Test
    void deveLancarNotFoundExceptionQuandoObraNaoEncontrada() {
        when(obraRepository.findByIdExternoAndTenantIdAndAtivoIsTrue(obraIdExterno, tenantId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getObraByIdExternoAndTenantIdService.execute(obraIdExterno, tenantId)
        );

        assertNotNull(exception);
        assertEquals("Obra não encontrada com idExterno: " + obraIdExterno + " e tenantId: " + tenantId,
                exception.getReason());

        verify(obraRepository, times(1)).findByIdExternoAndTenantIdAndAtivoIsTrue(obraIdExterno, tenantId);
    }
}

