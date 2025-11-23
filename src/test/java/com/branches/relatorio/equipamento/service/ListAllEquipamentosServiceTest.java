package com.branches.relatorio.equipamento.service;

import com.branches.equipamento.service.ListAllEquipamentosService;
import com.branches.exception.ForbiddenException;
import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.dto.response.EquipamentoResponse;
import com.branches.equipamento.repository.EquipamentoRepository;
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
class ListAllEquipamentosServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @InjectMocks
    private ListAllEquipamentosService listAllEquipamentosService;

    private String tenantExternalId;
    private Long tenantId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private UserTenantEntity userTenantWithoutAccess;
    private List<EquipamentoEntity> equipamentoEntityList;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;

        Authorities authoritiesWithAccess = Authorities.builder()
                .cadastros(PermissionsCadastro.builder()
                        .equipamentos(true)
                        .build())
                .build();

        Authorities authoritiesWithoutAccess = Authorities.builder()
                .cadastros(PermissionsCadastro.builder()
                        .equipamentos(false)
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

        EquipamentoEntity equipamento1 = EquipamentoEntity.builder()
                .id(1L)
                .descricao("Equipamento 1")
                .ativo(true)
                .tenantId(tenantId)
                .build();

        EquipamentoEntity equipamento2 = EquipamentoEntity.builder()
                .id(2L)
                .descricao("Equipamento 2")
                .ativo(true)
                .tenantId(tenantId)
                .build();

        equipamentoEntityList = List.of(equipamento1, equipamento2);
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(equipamentoRepository.findAllByTenantIdAndAtivoIsTrue(tenantId)).thenReturn(equipamentoEntityList);

        List<EquipamentoResponse> response = listAllEquipamentosService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals("Equipamento 1", response.get(0).descricao());
        assertEquals(2L, response.get(1).id());
        assertEquals("Equipamento 2", response.get(1).descricao());
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithoutAccess);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> listAllEquipamentosService.execute(tenantExternalId, userTenants)
        );

        assertNotNull(exception);
    }
}

