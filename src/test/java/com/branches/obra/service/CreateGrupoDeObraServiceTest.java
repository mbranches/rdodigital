package com.branches.obra.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.controller.CreateGrupoDeObraRequest;
import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.obra.dto.response.CreateGrupoDeObraResponse;
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
class CreateGrupoDeObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GrupoDeObraRepository grupoDeObraRepository;

    @InjectMocks
    private CreateGrupoDeObraService createGrupoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private CreateGrupoDeObraRequest request;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private UserTenantEntity userTenantWithoutAccess;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        request = new CreateGrupoDeObraRequest("Grupo Teste");

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
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        GrupoDeObraEntity savedEntity = GrupoDeObraEntity.builder()
                .id(1L)
                .descricao("Grupo Teste")
                .ativo(true)
                .build();
        savedEntity.setTenantId(tenantId);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(grupoDeObraRepository.save(any(GrupoDeObraEntity.class))).thenReturn(savedEntity);

        CreateGrupoDeObraResponse response = createGrupoDeObraService.execute(
                tenantExternalId,
                request,
                userTenants
        );

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.descricao(), response.descricao());
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithoutAccess);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> createGrupoDeObraService.execute(
                        tenantExternalId,
                        request,
                        userTenants
                )
        );

        assertNotNull(exception);
    }
}

