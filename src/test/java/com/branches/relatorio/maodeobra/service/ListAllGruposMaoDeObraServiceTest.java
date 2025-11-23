package com.branches.relatorio.maodeobra.service;

import com.branches.maodeobra.domain.GrupoMaoDeObraEntity;
import com.branches.maodeobra.dto.response.GrupoMaoDeObraResponse;
import com.branches.maodeobra.repository.GrupoMaoDeObraRepository;
import com.branches.maodeobra.service.CheckIfUserHasAccessToMaoDeObraService;
import com.branches.maodeobra.service.ListAllGruposMaoDeObraService;
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
class ListAllGruposMaoDeObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GrupoMaoDeObraRepository grupoMaoDeObraRepository;

    @InjectMocks
    private ListAllGruposMaoDeObraService listAllGruposMaoDeObraService;

    @Mock
    private CheckIfUserHasAccessToMaoDeObraService checkIfUserHasAccessToMaoDeObraService;

    private String tenantExternalId;
    private Long tenantId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private List<GrupoMaoDeObraEntity> grupoMaoDeObraEntityList;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;

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

        GrupoMaoDeObraEntity grupo1 = GrupoMaoDeObraEntity.builder()
                .id(1L)
                .descricao("Grupo Mão de Obra 1")
                .ativo(true)
                .build();
        grupo1.setTenantId(tenantId);

        GrupoMaoDeObraEntity grupo2 = GrupoMaoDeObraEntity.builder()
                .id(2L)
                .descricao("Grupo Mão de Obra 2")
                .ativo(true)
                .build();
        grupo2.setTenantId(tenantId);

        grupoMaoDeObraEntityList = List.of(grupo1, grupo2);
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(grupoMaoDeObraRepository.findAllByTenantIdAndAtivoIsTrue(tenantId)).thenReturn(grupoMaoDeObraEntityList);
        doNothing().when(checkIfUserHasAccessToMaoDeObraService).execute(userTenantWithAccess);

        List<GrupoMaoDeObraResponse> response = listAllGruposMaoDeObraService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().id());
        assertEquals("Grupo Mão de Obra 1", response.get(0).descricao());
        assertEquals(2L, response.get(1).id());
        assertEquals("Grupo Mão de Obra 2", response.get(1).descricao());
    }
}

