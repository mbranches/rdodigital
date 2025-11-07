package com.branches.obra.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.StatusObra;
import com.branches.obra.domain.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.repository.ObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.PermissionsDefault;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.UserTenantAuthorities;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @InjectMocks
    private DeleteObraService deleteObraService;

    private ObraEntity obraEntity;
    private String obraExternalId;
    private String tenantExternalId;
    private Long tenantId;
    private Long obraId;
    private List<UserTenantEntity> userTenants;
    private UserTenantAuthorities authorityCanDelete;
    private UserTenantAuthorities authorityCannotDelete;

    @BeforeEach
    void setUp() {
        obraExternalId = "obra-ext-123";
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        obraId = 1L;

        obraEntity = ObraEntity.builder()
                .id(obraId)
                .idExterno(obraExternalId)
                .nome("Obra Teste")
                .responsavel("João Silva")
                .contratante("Contratante Teste")
                .tipoContrato(TipoContratoDeObra.CONTRATADA)
                .dataInicio(LocalDate.of(2025, 1, 1))
                .dataPrevistaFim(LocalDate.of(2025, 12, 31))
                .numeroContrato("CONT-2025-001")
                .endereco("Rua Teste, 123")
                .observacoes("Observações de teste")
                .tipoMaoDeObra(TipoMaoDeObra.PERSONALIZADA)
                .status(StatusObra.EM_ANDAMENTO)
                .ativo(true)
                .build();
        obraEntity.setTenantId(tenantId);

        authorityCanDelete = UserTenantAuthorities.builder()
                .obras(
                        PermissionsDefault.builder()
                                .canDelete(true)
                                .build()
                )
                .build();

        authorityCannotDelete = UserTenantAuthorities.builder()
                .obras(
                        PermissionsDefault.builder()
                                .canDelete(false)
                                .build()
                )
                .build();
    }

    @Test
    void deveSetarObraComoInativaComSucesso() {
        // Arrange
        UserTenantEntity userTenant = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .authorities(authorityCanDelete)
                .build();
        userTenant.setUserObraPermitidaEntities(
                Set.of(
                        UserObraPermitidaEntity.builder()
                                .userTenant(userTenant)
                                .obraId(obraId)
                                .build()
                )
        );

        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraEntity);

        assertDoesNotThrow(() -> deleteObraService.execute(
                obraExternalId,
                tenantExternalId,
                userTenants
        ));

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(getObraByIdExternoAndTenantIdService, times(1)).execute(obraExternalId, tenantId);
        verify(obraRepository, times(1)).save(obraEntity);

        assertFalse(obraEntity.getAtivo());
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoPodeDeletar() {
        UserTenantEntity userTenant = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .authorities(authorityCannotDelete)
                .build();
        userTenant.setUserObraPermitidaEntities(
                Set.of(
                        UserObraPermitidaEntity.builder()
                                .userTenant(userTenant)
                                .obraId(obraId)
                                .build()
                )
        );

        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> deleteObraService.execute(
                        obraExternalId,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(getObraByIdExternoAndTenantIdService, times(1)).execute(obraExternalId, tenantId);
        verify(obraRepository, never()).save(any(ObraEntity.class));
        assertTrue(obraEntity.getAtivo()); // Obra deve permanecer ativa
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemAObra() {
        Long obraIdNaoPermitida = 999L;

        UserTenantEntity userTenant = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .authorities(authorityCanDelete)
                .build();
        userTenant.setUserObraPermitidaEntities(
                Set.of(
                        UserObraPermitidaEntity.builder()
                                .userTenant(userTenant)
                                .obraId(obraIdNaoPermitida) // ID diferente da obra que está sendo deletada
                                .build()
                )
        );

        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> deleteObraService.execute(
                        obraExternalId,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(getObraByIdExternoAndTenantIdService, times(1)).execute(obraExternalId, tenantId);
        verify(obraRepository, never()).save(any(ObraEntity.class));
        assertTrue(obraEntity.getAtivo()); // Obra deve permanecer ativa
    }
}

