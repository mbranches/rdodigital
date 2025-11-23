package com.branches.relatorio.equipamento.service;

import com.branches.equipamento.service.CreateEquipamentoService;
import com.branches.exception.ForbiddenException;
import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.dto.request.CreateEquipamentoRequest;
import com.branches.equipamento.dto.response.CreateEquipamentoResponse;
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
class CreateEquipamentoServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @InjectMocks
    private CreateEquipamentoService createEquipamentoService;

    private String tenantExternalId;
    private Long tenantId;
    private CreateEquipamentoRequest request;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private UserTenantEntity userTenantWithoutAccess;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        request = new CreateEquipamentoRequest("Equipamento Teste");

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
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        EquipamentoEntity savedEntity = EquipamentoEntity.builder()
                .id(1L)
                .descricao("Equipamento Teste")
                .ativo(true)
                .tenantId(tenantId)
                .build();

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(equipamentoRepository.save(any(EquipamentoEntity.class))).thenReturn(savedEntity);

        CreateEquipamentoResponse response = createEquipamentoService.execute(
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
                () -> createEquipamentoService.execute(
                        tenantExternalId,
                        request,
                        userTenants
                )
        );

        assertNotNull(exception);
    }
}

