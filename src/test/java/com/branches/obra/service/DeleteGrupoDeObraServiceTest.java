package com.branches.obra.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.obra.repository.GrupoDeObraRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteGrupoDeObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetGrupoDeObraByIdAndTenantIdService getGrupoDeObraByIdAndTenantIdService;

    @Mock
    private GrupoDeObraRepository grupoDeObraRepository;

    @InjectMocks
    private DeleteGrupoDeObraService deleteGrupoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private Long grupoId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private UserTenantEntity userTenantWithoutAccess;
    private GrupoDeObraEntity grupoDeObraEntity;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        grupoId = 1L;

        UserTenantAuthorities authoritiesWithAccess = UserTenantAuthorities.builder()
                .cadastros(PermissionsCadastro.builder()
                        .grupoDeObras(true)
                        .build())
                .build();

        UserTenantAuthorities authoritiesWithoutAccess = UserTenantAuthorities.builder()
                .cadastros(PermissionsCadastro.builder()
                        .grupoDeObras(false)
                        .build())
                .build();

        userTenantWithAccess = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .authorities(authoritiesWithAccess)
                .build();

        userTenantWithoutAccess = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .authorities(authoritiesWithoutAccess)
                .build();

        userTenants = List.of(userTenantWithAccess);

        grupoDeObraEntity = GrupoDeObraEntity.builder()
                .id(grupoId)
                .descricao("Grupo para deletar")
                .ativo(true)
                .build();
        grupoDeObraEntity.setTenantId(tenantId);
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId)).thenReturn(grupoDeObraEntity);
        when(grupoDeObraRepository.save(any(GrupoDeObraEntity.class))).thenReturn(grupoDeObraEntity);

        deleteGrupoDeObraService.execute(grupoId, tenantExternalId, userTenants);

        verify(grupoDeObraRepository, times(1)).save(argThat(grupo ->
                !grupo.isAtivo() &&
                grupo.getId().equals(grupoId)
        ));
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithoutAccess);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> deleteGrupoDeObraService.execute(grupoId, tenantExternalId, userTenants)
        );

        assertNotNull(exception);
        verify(grupoDeObraRepository, never()).save(any());
    }
}

