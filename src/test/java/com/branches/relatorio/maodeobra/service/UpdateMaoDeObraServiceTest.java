package com.branches.relatorio.maodeobra.service;

import com.branches.exception.BadRequestException;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.relatorio.maodeobra.domain.MaoDeObraEntity;
import com.branches.relatorio.maodeobra.dto.request.UpdateMaoDeObraRequest;
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
class UpdateMaoDeObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetMaoDeObraByIdAndTenantIdService getMaoDeObraByIdService;

    @Mock
    private GetGrupoMaoDeObraByIdAndTenantIdService getGrupoMaoDeObraByIdAndTenantIdService;

    @Mock
    private GetHorasTotais getHorasTotais;

    @Mock
    private MaoDeObraRepository maoDeObraRepository;

    @Mock
    private CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    @InjectMocks
    private UpdateMaoDeObraService updateMaoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private Long maoDeObraId;
    private Long grupoId;
    private Long novoGrupoId;
    private UpdateMaoDeObraRequest requestPersonalizada;
    private UpdateMaoDeObraRequest requestGenerica;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private MaoDeObraEntity maoDeObraPersonalizada;
    private MaoDeObraEntity maoDeObraGenerica;
    private GrupoMaoDeObraEntity grupoMaoDeObra;
    private GrupoMaoDeObraEntity novoGrupoMaoDeObra;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        maoDeObraId = 1L;
        grupoId = 1L;
        novoGrupoId = 2L;

        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaFim = LocalTime.of(17, 0);
        LocalTime horasIntervalo = LocalTime.of(1, 0);
        LocalTime horasTrabalhadas = LocalTime.of(8, 0);

        requestPersonalizada = new UpdateMaoDeObraRequest(
                "Mestre de Obras",
                novoGrupoId,
                "João Silva Atualizado",
                LocalTime.of(7, 0),
                LocalTime.of(16, 0),
                LocalTime.of(1, 30)
        );

        requestGenerica = new UpdateMaoDeObraRequest(
                "Ajudante",
                novoGrupoId,
                null,
                null,
                null,
                null
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
                .descricao("Grupo Original")
                .ativo(true)
                .build();
        grupoMaoDeObra.setTenantId(tenantId);

        novoGrupoMaoDeObra = GrupoMaoDeObraEntity.builder()
                .id(novoGrupoId)
                .descricao("Novo Grupo")
                .ativo(true)
                .build();
        novoGrupoMaoDeObra.setTenantId(tenantId);

        maoDeObraPersonalizada = MaoDeObraEntity.builder()
                .id(maoDeObraId)
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
        maoDeObraPersonalizada.setTenantId(tenantId);

        maoDeObraGenerica = MaoDeObraEntity.builder()
                .id(2L)
                .funcao("Servente")
                .tipo(TipoMaoDeObra.GENERICA)
                .grupo(grupoMaoDeObra)
                .ativo(true)
                .build();
        maoDeObraGenerica.setTenantId(tenantId);
    }

    @Test
    void deveExecutarComSucessoQuandoAtualizarMaoDeObraPersonalizada() {
        LocalTime novasHorasTrabalhadas = LocalTime.of(7, 30);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);
        when(getMaoDeObraByIdService.execute(maoDeObraId, tenantId)).thenReturn(maoDeObraPersonalizada);
        when(getGrupoMaoDeObraByIdAndTenantIdService.execute(novoGrupoId, tenantId)).thenReturn(novoGrupoMaoDeObra);
        when(getHorasTotais.execute(
                requestPersonalizada.horaInicio(),
                requestPersonalizada.horaFim(),
                requestPersonalizada.horasIntervalo()
        )).thenReturn(novasHorasTrabalhadas);
        when(maoDeObraRepository.save(any(MaoDeObraEntity.class))).thenReturn(maoDeObraPersonalizada);

        updateMaoDeObraService.execute(requestPersonalizada, maoDeObraId, tenantExternalId, userTenants);
}

    @Test
    void deveExecutarComSucessoQuandoAtualizarMaoDeObraGenerica() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);
        when(getMaoDeObraByIdService.execute(2L, tenantId)).thenReturn(maoDeObraGenerica);
        when(getGrupoMaoDeObraByIdAndTenantIdService.execute(novoGrupoId, tenantId)).thenReturn(novoGrupoMaoDeObra);
        when(maoDeObraRepository.save(any(MaoDeObraEntity.class))).thenReturn(maoDeObraGenerica);

        updateMaoDeObraService.execute(requestGenerica, 2L, tenantExternalId, userTenants);
    }

    @Test
    void deveLancarBadRequestExceptionQuandoTipoPersonalizadaENomeNulo() {
        UpdateMaoDeObraRequest requestInvalido = new UpdateMaoDeObraRequest(
                "Pedreiro",
                novoGrupoId,
                null,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                LocalTime.of(1, 0)
        );

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);
        when(getMaoDeObraByIdService.execute(maoDeObraId, tenantId)).thenReturn(maoDeObraPersonalizada);
        when(getGrupoMaoDeObraByIdAndTenantIdService.execute(novoGrupoId, tenantId)).thenReturn(novoGrupoMaoDeObra);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> updateMaoDeObraService.execute(
                        requestInvalido,
                        maoDeObraId,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
        assertEquals("Nome é obrigatório para mão de obra do tipo PERSONALIZADA", exception.getReason());

    }

    @Test
    void deveLancarBadRequestExceptionQuandoTipoPersonalizadaENomeVazio() {
        UpdateMaoDeObraRequest requestInvalido = new UpdateMaoDeObraRequest(
                "Pedreiro",
                novoGrupoId,
                "   ",
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                LocalTime.of(1, 0)
        );

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);
        when(getMaoDeObraByIdService.execute(maoDeObraId, tenantId)).thenReturn(maoDeObraPersonalizada);
        when(getGrupoMaoDeObraByIdAndTenantIdService.execute(novoGrupoId, tenantId)).thenReturn(novoGrupoMaoDeObra);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> updateMaoDeObraService.execute(
                        requestInvalido,
                        maoDeObraId,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
        assertEquals("Nome é obrigatório para mão de obra do tipo PERSONALIZADA", exception.getReason());

    }
}

