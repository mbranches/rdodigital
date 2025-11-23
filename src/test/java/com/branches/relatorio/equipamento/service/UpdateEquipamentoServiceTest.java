package com.branches.relatorio.equipamento.service;

import com.branches.equipamento.service.GetEquipamentoByIdAndTenantIdService;
import com.branches.equipamento.service.UpdateEquipamentoService;
import com.branches.exception.ForbiddenException;
import com.branches.equipamento.domain.EquipamentoEntity;
import com.branches.equipamento.dto.request.UpdateEquipamentoRequest;
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
class UpdateEquipamentoServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetEquipamentoByIdAndTenantIdService getEquipamentoByIdAndTenantIdService;

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @InjectMocks
    private UpdateEquipamentoService updateEquipamentoService;

    private String tenantExternalId;
    private Long tenantId;
    private Long equipamentoId;
    private UpdateEquipamentoRequest request;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private UserTenantEntity userTenantWithoutAccess;
    private EquipamentoEntity equipamentoEntity;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        equipamentoId = 1L;
        request = new UpdateEquipamentoRequest("Equipamento Atualizado");

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

        equipamentoEntity = EquipamentoEntity.builder()
                .id(equipamentoId)
                .descricao("Equipamento Original")
                .ativo(true)
                .tenantId(tenantId)
                .build();
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(getEquipamentoByIdAndTenantIdService.execute(equipamentoId, tenantId)).thenReturn(equipamentoEntity);
        when(equipamentoRepository.save(any(EquipamentoEntity.class))).thenReturn(equipamentoEntity);

        updateEquipamentoService.execute(equipamentoId, request, tenantExternalId, userTenants);

        verify(equipamentoRepository, times(1)).save(argThat(equipamento ->
                equipamento.getDescricao().equals("Equipamento Atualizado") &&
                equipamento.getId().equals(equipamentoId) &&
                equipamento.getAtivo()
        ));
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithoutAccess);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> updateEquipamentoService.execute(equipamentoId, request, tenantExternalId, userTenants)
        );

        assertNotNull(exception);
        verify(equipamentoRepository, never()).save(any());
    }
}

