package com.branches.relatorio.maodeobra.service;

import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.dto.request.CreateGrupoMaoDeObraRequest;
import com.branches.maodeobra.dto.response.CreateGrupoMaoDeObraResponse;
import com.branches.maodeobra.repository.GrupoMaoDeObraRepository;
import com.branches.maodeobra.service.CheckIfUserHasAccessToMaoDeObraService;
import com.branches.maodeobra.service.CreateGrupoMaoDeObraService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGrupoMaoDeObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GrupoMaoDeObraRepository grupoMaoDeObraRepository;

    @InjectMocks
    private CreateGrupoMaoDeObraService createGrupoMaoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private CreateGrupoMaoDeObraRequest request;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;

    @Mock
    private CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        request = new CreateGrupoMaoDeObraRequest("Grupo Mão de Obra Teste");

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
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        GrupoMaoDeObraEntity savedEntity = GrupoMaoDeObraEntity.builder()
                .id(1L)
                .descricao("Grupo Mão de Obra Teste")
                .ativo(true)
                .build();
        savedEntity.setTenantId(tenantId);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(grupoMaoDeObraRepository.save(any(GrupoMaoDeObraEntity.class))).thenReturn(savedEntity);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);

        CreateGrupoMaoDeObraResponse response = createGrupoMaoDeObraService.execute(
                tenantExternalId,
                request,
                userTenants
        );

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.descricao(), response.descricao());
    }
}
