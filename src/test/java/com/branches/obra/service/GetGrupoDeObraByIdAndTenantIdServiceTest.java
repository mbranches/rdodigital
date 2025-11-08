package com.branches.obra.service;

import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.exception.NotFoundException;
import com.branches.obra.repository.GrupoDeObraRepository;
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
class GetGrupoDeObraByIdAndTenantIdServiceTest {

    @Mock
    private GrupoDeObraRepository grupoDeObraRepository;

    @InjectMocks
    private GetGrupoDeObraByIdAndTenantIdService getGrupoDeObraByIdAndTenantIdService;

    private Long grupoId;
    private Long tenantId;
    private GrupoDeObraEntity grupoDeObra;

    @BeforeEach
    void setUp() {
        grupoId = 1L;
        tenantId = 1L;

        grupoDeObra = GrupoDeObraEntity.builder()
                .id(grupoId)
                .descricao("Grupo de Obras Teste")
                .build();
        grupoDeObra.setTenantId(tenantId);
    }

    @Test
    void deveRetornarGrupoDeObraQuandoEncontrado() {
        when(grupoDeObraRepository.findByIdAndTenantId(grupoId, tenantId))
                .thenReturn(Optional.of(grupoDeObra));

        GrupoDeObraEntity result = getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId);

        assertNotNull(result);
        assertEquals(grupoId, result.getId());
        assertEquals("Grupo de Obras Teste", result.getDescricao());
        assertEquals(tenantId, result.getTenantId());
    }

    @Test
    void deveLancarNotFoundExceptionQuandoGrupoNaoEncontrado() {
        when(grupoDeObraRepository.findByIdAndTenantId(grupoId, tenantId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId)
        );

        assertNotNull(exception);
        assertEquals("Grupo de Obra não encontrado com id: " + grupoId + " e tenantId: " + tenantId,
                     exception.getReason());
    }
}

