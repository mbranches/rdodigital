package com.branches.obra.service;

import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.dto.response.ObraByListAllResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.obra.repository.projections.ObraProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.UserTenantKey;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListAllObrasServiceTest {

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @InjectMocks
    private ListAllObrasService listAllObrasService;

    private String tenantExternalId;
    private Long tenantId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantAdministrador;
    private UserTenantEntity userTenantPersonalizado;
    private List<ObraProjection> obrasProjection;
    private ObraProjection obraProjection1;
    private ObraProjection obraProjection2;
    private ObraProjection obraProjection3;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;

        userTenantAdministrador = UserTenantEntity.builder()
                .id(UserTenantKey.from(100L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(100L).build())
                .perfil(PerfilUserTenant.ADMINISTRADOR)
                .ativo(true)
                .userObraPermitidaEntities(new HashSet<>())
                .build();

        UserObraPermitidaEntity obraPermitida1 = UserObraPermitidaEntity.builder()
                .obraId(1L)
                .build();

        UserObraPermitidaEntity obraPermitida2 = UserObraPermitidaEntity.builder()
                .obraId(2L)
                .build();

        Set<UserObraPermitidaEntity> obrasPermitidas = new HashSet<>();
        obrasPermitidas.add(obraPermitida1);
        obrasPermitidas.add(obraPermitida2);

        userTenantPersonalizado = UserTenantEntity.builder()
                .id(UserTenantKey.from(101L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(101L).build())
                .perfil(PerfilUserTenant.PERSONALIZADO)
                .ativo(true)
                .userObraPermitidaEntities(obrasPermitidas)
                .build();

        obraProjection1 = createObraProjection("obra-ext-1", "Obra 1", StatusObra.EM_ANDAMENTO, "http://capa1.jpg", 5L);
        obraProjection2 = createObraProjection("obra-ext-2", "Obra 2", StatusObra.CONCLUIDA, "http://capa2.jpg", 10L);
        obraProjection3 = createObraProjection("obra-ext-3", "Obra 3", StatusObra.EM_ANDAMENTO, "http://capa3.jpg", 2L);

        obrasProjection = List.of(obraProjection1, obraProjection2, obraProjection3);
    }

    @Test
    void deveListarTodasAsObrasQuandoUsuarioForAdministrador() {
        userTenants = List.of(userTenantAdministrador);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantAdministrador);
        when(obraRepository.findAllByTenantIdProjection(tenantId)).thenReturn(obrasProjection);

        List<ObraByListAllResponse> response = listAllObrasService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals(3, response.size());

        assertEquals("obra-ext-1", response.getFirst().id());
        assertEquals("Obra 1", response.getFirst().nome());
        assertEquals(StatusObra.EM_ANDAMENTO, response.getFirst().status());
        assertEquals("http://capa1.jpg", response.getFirst().capaUrl());
        assertEquals(5L, response.getFirst().quantityOfRelatorios());

        assertEquals("obra-ext-2", response.get(1).id());
        assertEquals("Obra 2", response.get(1).nome());
        assertEquals(StatusObra.CONCLUIDA, response.get(1).status());

        assertEquals("obra-ext-3", response.get(2).id());
        assertEquals("Obra 3", response.get(2).nome());
        assertEquals(StatusObra.EM_ANDAMENTO, response.get(2).status());

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(obraRepository, times(1)).findAllByTenantIdProjection(tenantId);
        verify(obraRepository, never()).findAllByTenantIdAndIdInProjection(anyLong(), anyList());
    }

    @Test
    void deveListarApenasObrasPermitidasQuandoUsuarioForPersonalizado() {
        userTenants = List.of(userTenantPersonalizado);

        List<Long> obrasPermitidasIds = List.of(1L, 2L);
        List<ObraProjection> obrasPermitidas = List.of(obraProjection1, obraProjection2);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantPersonalizado);
        when(obraRepository.findAllByTenantIdAndIdInProjection(eq(tenantId), any()))
                .thenReturn(obrasPermitidas);

        List<ObraByListAllResponse> response = listAllObrasService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals("obra-ext-1", response.getFirst().id());
        assertEquals("Obra 1", response.getFirst().nome());

        assertEquals("obra-ext-2", response.get(1).id());
        assertEquals("Obra 2", response.get(1).nome());

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(obraRepository, times(1)).findAllByTenantIdAndIdInProjection(eq(tenantId), any());
        verify(obraRepository, never()).findAllByTenantIdProjection(anyLong());
    }

    @Test
    void deveListarApenasObrasPermitidasQuandoUsuarioForClienteObra() {
        UserTenantEntity userTenantClienteObra = UserTenantEntity.builder()
                .id(UserTenantKey.from(102L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(102L).build())
                .perfil(PerfilUserTenant.CLIENTE_OBRA)
                .ativo(true)
                .build();

        UserObraPermitidaEntity obraPermitida = UserObraPermitidaEntity.builder()
                .obraId(1L)
                .build();

        Set<UserObraPermitidaEntity> obrasPermitidas = new HashSet<>();
        obrasPermitidas.add(obraPermitida);
        userTenantClienteObra.setUserObraPermitidaEntities(obrasPermitidas);

        userTenants = List.of(userTenantClienteObra);

        List<Long> obrasPermitidasIds = List.of(1L);
        List<ObraProjection> obrasPermitidasList = List.of(obraProjection1);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantClienteObra);
        when(obraRepository.findAllByTenantIdAndIdInProjection(tenantId, obrasPermitidasIds))
                .thenReturn(obrasPermitidasList);

        List<ObraByListAllResponse> response = listAllObrasService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("obra-ext-1", response.getFirst().id());
        assertEquals("Obra 1", response.getFirst().nome());
        assertEquals(StatusObra.EM_ANDAMENTO, response.getFirst().status());

        verify(obraRepository, times(1)).findAllByTenantIdAndIdInProjection(tenantId, obrasPermitidasIds);
        verify(obraRepository, never()).findAllByTenantIdProjection(anyLong());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverObras() {
        userTenants = List.of(userTenantAdministrador);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantAdministrador);
        when(obraRepository.findAllByTenantIdProjection(tenantId)).thenReturn(List.of());

        List<ObraByListAllResponse> response = listAllObrasService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(obraRepository, times(1)).findAllByTenantIdProjection(tenantId);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioPersonalizadoNaoTiverObrasPermitidas() {
        UserTenantEntity userSemObras = UserTenantEntity.builder()
                .id(UserTenantKey.from(103L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(103L).build())
                .perfil(PerfilUserTenant.PERSONALIZADO)
                .ativo(true)
                .userObraPermitidaEntities(new HashSet<>())
                .build();

        userTenants = List.of(userSemObras);

        List<Long> obrasPermitidasIds = List.of();

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userSemObras);
        when(obraRepository.findAllByTenantIdAndIdInProjection(tenantId, obrasPermitidasIds))
                .thenReturn(List.of());

        List<ObraByListAllResponse> response = listAllObrasService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(obraRepository, times(1)).findAllByTenantIdAndIdInProjection(tenantId, obrasPermitidasIds);
        verify(obraRepository, never()).findAllByTenantIdProjection(anyLong());
    }

    @Test
    void deveListarObrasComQuantityOfRelatoriosZero() {
        ObraProjection obraSemRelatorios = createObraProjection(
                "obra-ext-sem-relatorios",
                "Obra Sem Relatórios",
                StatusObra.EM_ANDAMENTO,
                null,
                0L
        );

        userTenants = List.of(userTenantAdministrador);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantAdministrador);
        when(obraRepository.findAllByTenantIdProjection(tenantId)).thenReturn(List.of(obraSemRelatorios));

        List<ObraByListAllResponse> response = listAllObrasService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("obra-ext-sem-relatorios", response.getFirst().id());
        assertEquals("Obra Sem Relatórios", response.getFirst().nome());
        assertEquals(0L, response.getFirst().quantityOfRelatorios());
        assertNull(response.getFirst().capaUrl());

        verify(obraRepository, times(1)).findAllByTenantIdProjection(tenantId);
    }

    private ObraProjection createObraProjection(String idExterno, String nome, StatusObra status, String capaUrl, Long quantityOfRelatorios) {
        return new ObraProjection() {
            @Override
            public String getIdExterno() {
                return idExterno;
            }

            @Override
            public String getNome() {
                return nome;
            }

            @Override
            public String getCapaUrl() {
                return capaUrl;
            }

            @Override
            public StatusObra getStatus() {
                return status;
            }

            @Override
            public Long getQuantityOfRelatorios() {
                return quantityOfRelatorios;
            }
        };
    }
}

