package com.branches.relatorio.equipamento.service;

import com.branches.equipamento.service.GetEquipamentoByIdAndTenantIdService;
import com.branches.exception.NotFoundException;
import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.repository.EquipamentoRepository;
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
class GetEquipamentoByIdAndTenantIdServiceTest {

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @InjectMocks
    private GetEquipamentoByIdAndTenantIdService getEquipamentoByIdAndTenantIdService;

    private Long equipamentoId;
    private Long tenantId;
    private EquipamentoEntity equipamentoEntity;

    @BeforeEach
    void setUp() {
        equipamentoId = 1L;
        tenantId = 1L;

        equipamentoEntity = EquipamentoEntity.builder()
                .id(equipamentoId)
                .descricao("Equipamento Teste")
                .ativo(true)
                .build();
        equipamentoEntity.setTenantId(tenantId);
    }

    @Test
    void deveRetornarEquipamentoQuandoEncontrado() {
        when(equipamentoRepository.findByIdAndTenantIdAndAtivoIsTrue(equipamentoId, tenantId))
                .thenReturn(Optional.of(equipamentoEntity));

        EquipamentoEntity result = getEquipamentoByIdAndTenantIdService.execute(equipamentoId, tenantId);

        assertNotNull(result);
        assertEquals(equipamentoId, result.getId());
        assertEquals("Equipamento Teste", result.getDescricao());
        assertEquals(tenantId, result.getTenantId());
        assertTrue(result.getAtivo());

        verify(equipamentoRepository, times(1)).findByIdAndTenantIdAndAtivoIsTrue(equipamentoId, tenantId);
    }

    @Test
    void deveLancarNotFoundExceptionQuandoEquipamentoNaoEncontrado() {
        when(equipamentoRepository.findByIdAndTenantIdAndAtivoIsTrue(equipamentoId, tenantId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getEquipamentoByIdAndTenantIdService.execute(equipamentoId, tenantId)
        );

        assertNotNull(exception);
        assertEquals("Equipamento não encontrado com o id: " + equipamentoId,
                     exception.getReason());

    }
}

