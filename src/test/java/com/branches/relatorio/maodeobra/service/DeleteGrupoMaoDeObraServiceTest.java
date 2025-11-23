package com.branches.relatorio.maodeobra.service;

import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.repository.GrupoMaoDeObraRepository;
import com.branches.maodeobra.service.CheckIfUserHasAccessToMaoDeObraService;
import com.branches.maodeobra.service.DeleteGrupoMaoDeObraService;
import com.branches.maodeobra.service.GetGrupoMaoDeObraByIdAndTenantIdService;
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

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteGrupoMaoDeObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetGrupoMaoDeObraByIdAndTenantIdService getGrupoMaoDeObraByIdAndTenantIdService;

    @Mock
    private GrupoMaoDeObraRepository grupoMaoDeObraRepository;

    @InjectMocks
    private DeleteGrupoMaoDeObraService deleteGrupoMaoDeObraService;

    @Mock
    private CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private Long grupoId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private GrupoMaoDeObraEntity grupoMaoDeObraEntity;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        grupoId = 1L;

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

        grupoMaoDeObraEntity = GrupoMaoDeObraEntity.builder()
                .id(grupoId)
                .descricao("Grupo para deletar")
                .ativo(true)
                .build();
        grupoMaoDeObraEntity.setTenantId(tenantId);
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(getGrupoMaoDeObraByIdAndTenantIdService.execute(grupoId, tenantId)).thenReturn(grupoMaoDeObraEntity);
        when(grupoMaoDeObraRepository.save(any(GrupoMaoDeObraEntity.class))).thenReturn(grupoMaoDeObraEntity);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);

        deleteGrupoMaoDeObraService.execute(grupoId, tenantExternalId, userTenants);

        verify(grupoMaoDeObraRepository, times(1)).save(argThat(grupo ->
                !grupo.isAtivo() &&
                grupo.getId().equals(grupoId)
        ));
    }
}

