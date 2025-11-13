package com.branches.relatorio.maodeobra.service;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.dto.response.MaoDeObraResponse;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.PermissionsCadastro;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserTenantAuthorities;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListAllMaoDeObraServiceTest {

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private MaoDeObraRepository maoDeObraRepository;

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    @InjectMocks
    private ListAllMaoDeObraService listAllMaoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private GrupoMaoDeObraEntity grupo;
    private List<MaoDeObraEntity> maoDeObraEntityList;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;

        UserTenantAuthorities authoritiesWithAccess = UserTenantAuthorities.builder()
                .cadastros(PermissionsCadastro.builder()
                        .maoDeObra(true)
                        .build())
                .build();

        userTenantWithAccess = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .authorities(authoritiesWithAccess)
                .build();

        userTenants = List.of(userTenantWithAccess);

        grupo = GrupoMaoDeObraEntity.builder()
                .id(1L)
                .descricao("Grupo Teste")
                .ativo(true)
                .build();
        grupo.setTenantId(tenantId);

        MaoDeObraEntity maoDeObra1 = MaoDeObraEntity.builder()
                .id(1L)
                .funcao("Pedreiro")
                .grupo(grupo)
                .nome("João Silva")
                .horaInicio(LocalTime.of(8, 0))
                .horaFim(LocalTime.of(17, 0))
                .horasIntervalo(LocalTime.of(1, 0))
                .horasTrabalhadas(LocalTime.of(8, 0))
                .tipo(TipoMaoDeObra.PERSONALIZADA)
                .ativo(true)
                .build();
        maoDeObra1.setTenantId(tenantId);

        MaoDeObraEntity maoDeObra2 = MaoDeObraEntity.builder()
                .id(2L)
                .funcao("Servente")
                .grupo(grupo)
                .nome("Maria Santos")
                .horaInicio(LocalTime.of(8, 0))
                .horaFim(LocalTime.of(17, 0))
                .horasIntervalo(LocalTime.of(1, 0))
                .horasTrabalhadas(LocalTime.of(8, 0))
                .tipo(TipoMaoDeObra.PERSONALIZADA)
                .ativo(true)
                .build();
        maoDeObra2.setTenantId(tenantId);

        maoDeObraEntityList = List.of(maoDeObra1, maoDeObra2);
    }

    @Test
    void deveExecutarComSucessoQuandoTipoIsPersonalizada() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(maoDeObraRepository.findAllByTenantIdAndTipo(tenantId, TipoMaoDeObra.PERSONALIZADA))
                .thenReturn(maoDeObraEntityList);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);

        List<MaoDeObraResponse> response = listAllMaoDeObraService.execute(
                tenantExternalId,
                TipoMaoDeObra.PERSONALIZADA,
                userTenants
        );

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().id());
        assertEquals("Pedreiro", response.getFirst().funcao());
        assertEquals("João Silva", response.getFirst().nome());
        assertEquals(1L, response.getFirst().grupo().id());
        assertEquals("Grupo Teste", response.getFirst().grupo().descricao());
        assertEquals(2L, response.get(1).id());
        assertEquals("Servente", response.get(1).funcao());
        assertEquals("Maria Santos", response.get(1).nome());

    }

    @Test
    void deveExecutarComSucessoQuandoTipoIsGenerica() {
        MaoDeObraEntity maoDeObraGenerica = MaoDeObraEntity.builder()
                .id(3L)
                .funcao("Eletricista")
                .grupo(grupo)
                .nome("Carlos Souza")
                .horaInicio(LocalTime.of(8, 0))
                .horaFim(LocalTime.of(17, 0))
                .horasIntervalo(LocalTime.of(1, 0))
                .horasTrabalhadas(LocalTime.of(8, 0))
                .tipo(TipoMaoDeObra.GENERICA)
                .ativo(true)
                .build();
        maoDeObraGenerica.setTenantId(tenantId);

        List<MaoDeObraEntity> maoDeObraGenericaList = List.of(maoDeObraGenerica);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(maoDeObraRepository.findAllByTenantIdAndTipo(tenantId, TipoMaoDeObra.GENERICA))
                .thenReturn(maoDeObraGenericaList);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);

        List<MaoDeObraResponse> response = listAllMaoDeObraService.execute(
                tenantExternalId,
                TipoMaoDeObra.GENERICA,
                userTenants
        );

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(3L, response.getFirst().id());
        assertEquals("Eletricista", response.getFirst().funcao());
        assertEquals("Carlos Souza", response.getFirst().nome());

    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExisteMaoDeObra() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(maoDeObraRepository.findAllByTenantIdAndTipo(tenantId, TipoMaoDeObra.PERSONALIZADA))
                .thenReturn(List.of());
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);

        List<MaoDeObraResponse> response = listAllMaoDeObraService.execute(
                tenantExternalId,
                TipoMaoDeObra.PERSONALIZADA,
                userTenants
        );

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}

