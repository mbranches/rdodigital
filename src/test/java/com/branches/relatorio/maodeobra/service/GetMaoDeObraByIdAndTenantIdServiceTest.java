package com.branches.relatorio.maodeobra.service;

import com.branches.exception.NotFoundException;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMaoDeObraByIdAndTenantIdServiceTest {

    @Mock
    private MaoDeObraRepository maoDeObraRepository;

    @InjectMocks
    private GetMaoDeObraByIdAndTenantIdService getMaoDeObraByIdAndTenantIdService;

    private Long maoDeObraId;
    private Long tenantId;
    private MaoDeObraEntity maoDeObraEntity;
    private GrupoMaoDeObraEntity grupoMaoDeObra;

    @BeforeEach
    void setUp() {
        maoDeObraId = 1L;
        tenantId = 1L;

        grupoMaoDeObra = GrupoMaoDeObraEntity.builder()
                .id(1L)
                .descricao("Grupo de Mão de Obra")
                .ativo(true)
                .build();

        maoDeObraEntity = MaoDeObraEntity.builder()
                .id(maoDeObraId)
                .nome("João Silva")
                .funcao("Pedreiro")
                .tipo(TipoMaoDeObra.PERSONALIZADA)
                .horaInicio(LocalTime.of(8, 0))
                .horaFim(LocalTime.of(17, 0))
                .horasIntervalo(LocalTime.of(1, 0))
                .horasTrabalhadas(LocalTime.of(8, 0))
                .grupo(grupoMaoDeObra)
                .ativo(true)
                .build();
        maoDeObraEntity.setTenantId(tenantId);
    }

    @Test
    void deveRetornarMaoDeObraQuandoEncontrada() {
        when(maoDeObraRepository.findByIdAndTenantIdIsTrue(maoDeObraId, tenantId))
                .thenReturn(Optional.of(maoDeObraEntity));

        MaoDeObraEntity result = getMaoDeObraByIdAndTenantIdService.execute(maoDeObraId, tenantId);

        assertNotNull(result);
        assertEquals(maoDeObraId, result.getId());
        assertEquals("João Silva", result.getNome());
        assertEquals("Pedreiro", result.getFuncao());
        assertEquals(TipoMaoDeObra.PERSONALIZADA, result.getTipo());
        assertEquals(tenantId, result.getTenantId());

        verify(maoDeObraRepository, times(1)).findByIdAndTenantIdIsTrue(maoDeObraId, tenantId);
    }

    @Test
    void deveLancarNotFoundExceptionQuandoMaoDeObraNaoEncontrada() {
        when(maoDeObraRepository.findByIdAndTenantIdIsTrue(maoDeObraId, tenantId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getMaoDeObraByIdAndTenantIdService.execute(maoDeObraId, tenantId)
        );

        assertNotNull(exception);
        assertEquals("Mão de obra com id " + maoDeObraId + " não encontrada para o tenant " + tenantId,
                     exception.getReason());

        verify(maoDeObraRepository, times(1)).findByIdAndTenantIdIsTrue(maoDeObraId, tenantId);
    }

}

