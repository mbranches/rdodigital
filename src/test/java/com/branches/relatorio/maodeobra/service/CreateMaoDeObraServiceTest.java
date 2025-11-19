package com.branches.relatorio.maodeobra.service;

import com.branches.exception.BadRequestException;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.dto.request.CreateMaoDeObraRequest;
import com.branches.relatorio.maodeobra.dto.response.CreateMaoDeObraResponse;
import com.branches.relatorio.maodeobra.repository.MaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.PermissionsCadastro;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.Authorities;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.GetHorasTotais;
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
class CreateMaoDeObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetGrupoMaoDeObraByIdAndTenantIdService getGrupoMaoDeObraByIdAndTenantIdService;

    @Mock
    private MaoDeObraRepository maoDeObraRepository;

    @Mock
    private GetHorasTotais getHorasTotais;

    @Mock
    private CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    @InjectMocks
    private CreateMaoDeObraService createMaoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private Long grupoId;
    private CreateMaoDeObraRequest requestPersonalizada;
    private CreateMaoDeObraRequest requestGenerica;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private GrupoMaoDeObraEntity grupoMaoDeObra;
    private MaoDeObraEntity savedMaoDeObraPersonalizada;
    private MaoDeObraEntity savedMaoDeObraGenerica;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        grupoId = 1L;

        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaFim = LocalTime.of(17, 0);
        LocalTime horasIntervalo = LocalTime.of(1, 0);
        LocalTime horasTrabalhadas = LocalTime.of(8, 0);

        requestPersonalizada = new CreateMaoDeObraRequest(
                "Pedreiro",
                grupoId,
                "João Silva",
                horaInicio,
                horaFim,
                horasIntervalo,
                TipoMaoDeObra.PERSONALIZADA
        );

        requestGenerica = new CreateMaoDeObraRequest(
                "Servente",
                grupoId,
                null,
                null,
                null,
                null,
                TipoMaoDeObra.GENERICA
        );

        Authorities authoritiesWithAccess = Authorities.builder()
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

        grupoMaoDeObra = GrupoMaoDeObraEntity.builder()
                .id(grupoId)
                .descricao("Grupo de Mão de Obra")
                .ativo(true)
                .build();
        grupoMaoDeObra.setTenantId(tenantId);

        savedMaoDeObraPersonalizada = MaoDeObraEntity.builder()
                .id(1L)
                .nome("João Silva")
                .funcao("Pedreiro")
                .tipo(TipoMaoDeObra.PERSONALIZADA)
                .horaInicio(horaInicio)
                .horaFim(horaFim)
                .horasIntervalo(horasIntervalo)
                .horasTrabalhadas(horasTrabalhadas)
                .grupo(grupoMaoDeObra)
                .ativo(true)
                .build();
        savedMaoDeObraPersonalizada.setTenantId(tenantId);

        savedMaoDeObraGenerica = MaoDeObraEntity.builder()
                .id(2L)
                .funcao("Servente")
                .tipo(TipoMaoDeObra.GENERICA)
                .grupo(grupoMaoDeObra)
                .ativo(true)
                .build();
        savedMaoDeObraGenerica.setTenantId(tenantId);
    }

    @Test
    void deveExecutarComSucessoQuandoCriarMaoDeObraPersonalizada() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);
        when(getGrupoMaoDeObraByIdAndTenantIdService.execute(grupoId, tenantId)).thenReturn(grupoMaoDeObra);
        when(getHorasTotais.execute(
                requestPersonalizada.horaInicio(),
                requestPersonalizada.horaFim(),
                requestPersonalizada.horasIntervalo()
        )).thenReturn(LocalTime.of(8, 0));
        when(maoDeObraRepository.save(any(MaoDeObraEntity.class))).thenReturn(savedMaoDeObraPersonalizada);

        CreateMaoDeObraResponse response = createMaoDeObraService.execute(
                requestPersonalizada,
                tenantExternalId,
                userTenants
        );

        assertNotNull(response);
        assertEquals(savedMaoDeObraPersonalizada.getId(), response.id());
        assertEquals(requestPersonalizada.nome(), response.nome());
        assertEquals(requestPersonalizada.funcao(), response.funcao());
        assertEquals(requestPersonalizada.horaInicio(), response.horaInicio());
        assertEquals(requestPersonalizada.horaFim(), response.horaFim());
        assertEquals(requestPersonalizada.horasIntervalo(), response.horasIntervalo());
        assertNotNull(response.horasTrabalhadas());
        assertNotNull(response.grupo());
        assertEquals(grupoMaoDeObra.getId(), response.grupo().id());
    }

    @Test
    void deveExecutarComSucessoQuandoCriarMaoDeObraGenerica() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);
        when(getGrupoMaoDeObraByIdAndTenantIdService.execute(grupoId, tenantId)).thenReturn(grupoMaoDeObra);
        when(maoDeObraRepository.save(any(MaoDeObraEntity.class))).thenReturn(savedMaoDeObraGenerica);

        CreateMaoDeObraResponse response = createMaoDeObraService.execute(
                requestGenerica,
                tenantExternalId,
                userTenants
        );

        assertNotNull(response);
        assertEquals(savedMaoDeObraGenerica.getId(), response.id());
        assertEquals(requestGenerica.funcao(), response.funcao());
        assertNull(response.nome());
        assertNull(response.horaInicio());
        assertNull(response.horaFim());
        assertNull(response.horasIntervalo());
        assertNull(response.horasTrabalhadas());
        assertNotNull(response.grupo());
        assertEquals(grupoMaoDeObra.getId(), response.grupo().id());
    }

    @Test
    void deveLancarBadRequestExceptionQuandoTipoPersonalizadaENomeNulo() {
        CreateMaoDeObraRequest requestInvalido = new CreateMaoDeObraRequest(
                "Pedreiro",
                grupoId,
                null,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                LocalTime.of(1, 0),
                TipoMaoDeObra.PERSONALIZADA
        );

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> createMaoDeObraService.execute(
                        requestInvalido,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
        assertEquals("Nome é obrigatório para mão de obra do tipo PERSONALIZADA", exception.getReason());
    }

    @Test
    void deveLancarBadRequestExceptionQuandoTipoPersonalizadaENomeVazio() {
        CreateMaoDeObraRequest requestInvalido = new CreateMaoDeObraRequest(
                "Pedreiro",
                grupoId,
                "   ",
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                LocalTime.of(1, 0),
                TipoMaoDeObra.PERSONALIZADA
        );

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> createMaoDeObraService.execute(
                        requestInvalido,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
        assertEquals("Nome é obrigatório para mão de obra do tipo PERSONALIZADA", exception.getReason());
    }

}

