package com.branches.relatorio.maodeobra.service;

import com.branches.exception.NotFoundException;
import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.repository.GrupoMaoDeObraRepository;
import com.branches.maodeobra.service.GetGrupoMaoDeObraByIdAndTenantIdService;
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
class GetGrupoMaoDeObraByIdAndTenantIdServiceTest {

    @Mock
    private GrupoMaoDeObraRepository grupoMaoDeObraRepository;

    @InjectMocks
    private GetGrupoMaoDeObraByIdAndTenantIdService getGrupoMaoDeObraByIdAndTenantIdService;

    private Long grupoId;
    private Long tenantId;
    private GrupoMaoDeObraEntity grupoMaoDeObra;

    @BeforeEach
    void setUp() {
        grupoId = 1L;
        tenantId = 1L;

        grupoMaoDeObra = GrupoMaoDeObraEntity.builder()
                .id(grupoId)
                .descricao("Grupo de Mão de Obra Teste")
                .ativo(true)
                .build();
        grupoMaoDeObra.setTenantId(tenantId);
    }

    @Test
    void deveRetornarGrupoMaoDeObraQuandoEncontrado() {
        when(grupoMaoDeObraRepository.findByIdAndTenantIdAndAtivoIsTrue(grupoId, tenantId))
                .thenReturn(Optional.of(grupoMaoDeObra));

        GrupoMaoDeObraEntity result = getGrupoMaoDeObraByIdAndTenantIdService.execute(grupoId, tenantId);

        assertNotNull(result);
        assertEquals(grupoId, result.getId());
        assertEquals("Grupo de Mão de Obra Teste", result.getDescricao());
        assertEquals(tenantId, result.getTenantId());
        assertTrue(result.isAtivo());
    }

    @Test
    void deveLancarNotFoundExceptionQuandoGrupoNaoEncontrado() {
        when(grupoMaoDeObraRepository.findByIdAndTenantIdAndAtivoIsTrue(grupoId, tenantId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getGrupoMaoDeObraByIdAndTenantIdService.execute(grupoId, tenantId)
        );

        assertNotNull(exception);
        assertEquals("Grupo de Mão de Obra não encontrado com id: " + grupoId + " e tenantId: " + tenantId,
                     exception.getReason());

    }
}

