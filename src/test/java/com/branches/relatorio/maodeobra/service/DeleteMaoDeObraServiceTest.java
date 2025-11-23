package com.branches.relatorio.maodeobra.service;

import com.branches.maodeobra.service.CheckIfUserHasAccessToMaoDeObraService;
import com.branches.maodeobra.service.DeleteMaoDeObraService;
import com.branches.maodeobra.service.GetMaoDeObraByIdAndTenantIdService;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.domain.MaoDeObraEntity;
import com.branches.maodeobra.repository.MaoDeObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.PermissionsCadastro;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.Authorities;
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
class DeleteMaoDeObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetMaoDeObraByIdAndTenantIdService getMaoDeObraByIdAndTenantIdService;

    @Mock
    private MaoDeObraRepository maoDeObraRepository;

    @Mock
    private CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    @InjectMocks
    private DeleteMaoDeObraService deleteMaoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private Long maoDeObraId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private MaoDeObraEntity maoDeObraEntityPersonalizada;
    private MaoDeObraEntity maoDeObraEntityGenerica;
    private GrupoMaoDeObraEntity grupoMaoDeObra;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        maoDeObraId = 1L;

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
                .id(1L)
                .descricao("Grupo de Mão de Obra")
                .ativo(true)
                .build();
        grupoMaoDeObra.setTenantId(tenantId);

        maoDeObraEntityPersonalizada = MaoDeObraEntity.builder()
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
        maoDeObraEntityPersonalizada.setTenantId(tenantId);

        maoDeObraEntityGenerica = MaoDeObraEntity.builder()
                .id(2L)
                .funcao("Servente")
                .tipo(TipoMaoDeObra.GENERICA)
                .grupo(grupoMaoDeObra)
                .ativo(true)
                .build();
        maoDeObraEntityGenerica.setTenantId(tenantId);
    }

    @Test
    void deveExecutarComSucesso() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);
        when(getMaoDeObraByIdAndTenantIdService.execute(maoDeObraId, tenantId)).thenReturn(maoDeObraEntityPersonalizada);
        when(maoDeObraRepository.save(any(MaoDeObraEntity.class))).thenReturn(maoDeObraEntityPersonalizada);

        assertDoesNotThrow(() -> deleteMaoDeObraService.execute(
                maoDeObraId,
                tenantExternalId,
                userTenants
        ));

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(checkIfUserHasAccessToMaoDeObraService, times(1)).execute(userTenantWithAccess);
        verify(getMaoDeObraByIdAndTenantIdService, times(1)).execute(maoDeObraId, tenantId);
        verify(maoDeObraRepository, times(1)).save(argThat(maoDeObra ->
                !maoDeObra.getAtivo() &&
                maoDeObra.getId().equals(maoDeObraId)
        ));
    }

}

