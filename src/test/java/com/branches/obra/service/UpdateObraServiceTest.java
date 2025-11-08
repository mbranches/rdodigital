package com.branches.obra.service;

import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.StatusObra;
import com.branches.obra.domain.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.dto.request.UpdateObraRequest;
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
class UpdateObraServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private GetGrupoDeObraByIdAndTenantIdService getGrupoDeObraByIdAndTenantIdService;

    @InjectMocks
    private UpdateObraService updateObraService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;

    private UpdateObraRequest updateObraRequest;
    private ObraEntity obraEntity;
    private GrupoDeObraEntity grupoDeObra;
    private String obraExternalId;
    private String tenantExternalId;
    private Long tenantId;
    private Long obraId;
    private Long grupoId;
    private List<UserTenantEntity> userTenants;
    private UserTenantAuthorities authorityCanEdit;
    private UserTenantAuthorities authorityCannotEdit;

    @BeforeEach
    void setUp() {
        obraExternalId = "obra-ext-123";
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        obraId = 1L;
        grupoId = 1L;

        grupoDeObra = GrupoDeObraEntity.builder()
                .id(grupoId)
                .descricao("Grupo Teste")
                .build();
        grupoDeObra.setTenantId(tenantId);

        updateObraRequest = new UpdateObraRequest(
                "Obra Atualizada",
                "Maria Silva",
                "Novo Contratante",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 11, 30),
                "CONT-2025-002",
                "Nova Rua, 456",
                "Novas observações",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.CONCLUIDA,
                grupoId
        );

        obraEntity = ObraEntity.builder()
                .id(obraId)
                .idExterno(obraExternalId)
                .nome("Obra Original")
                .responsavel("João Silva")
                .contratante("Contratante Original")
                .tipoContrato(TipoContratoDeObra.CONTRATADA)
                .dataInicio(LocalDate.of(2025, 1, 1))
                .dataPrevistaFim(LocalDate.of(2025, 12, 31))
                .numeroContrato("CONT-2025-001")
                .endereco("Rua Original, 123")
                .observacoes("Observações originais")
                .tipoMaoDeObra(TipoMaoDeObra.PERSONALIZADA)
                .status(StatusObra.EM_ANDAMENTO)
                .ativo(true)
                .build();
        obraEntity.setTenantId(tenantId);

        authorityCanEdit = UserTenantAuthorities.builder()
                .obras(
                        PermissionsDefault.builder()
                                .canCreateAndEdit(true)
                                .build()
                )
                .build();

        authorityCannotEdit = UserTenantAuthorities.builder()
                .obras(
                        PermissionsDefault.builder()
                                .canCreateAndEdit(false)
                                .build()
                )
                .build();
    }

    @Test
    void deveAtualizarObraComSucesso() {
        UserTenantEntity userTenant = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .build();
        userTenant.setUserObraPermitidaEntities(
                Set.of(
                        UserObraPermitidaEntity.builder()
                                .userTenant(userTenant)
                                .obraId(obraId)
                                .build()
                )
        );
        userTenant.setAuthorities(authorityCanEdit);

        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId))
                .thenReturn(grupoDeObra);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraEntity);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);

        assertDoesNotThrow(() -> updateObraService.execute(
                updateObraRequest,
                obraExternalId,
                tenantExternalId,
                userTenants
        ));

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getGrupoDeObraByIdAndTenantIdService, times(1)).execute(grupoId, tenantId);
        verify(obraRepository, times(1)).save(obraEntity);

        assertEquals("Obra Atualizada", obraEntity.getNome());
        assertEquals("Maria Silva", obraEntity.getResponsavel());
        assertEquals("Novo Contratante", obraEntity.getContratante());
        assertEquals(TipoContratoDeObra.CONTRATADA, obraEntity.getTipoContrato());
        assertEquals(LocalDate.of(2025, 2, 1), obraEntity.getDataInicio());
        assertEquals(LocalDate.of(2025, 11, 30), obraEntity.getDataPrevistaFim());
        assertEquals("CONT-2025-002", obraEntity.getNumeroContrato());
        assertEquals("Nova Rua, 456", obraEntity.getEndereco());
        assertEquals("Novas observações", obraEntity.getObservacoes());
        assertEquals(TipoMaoDeObra.PERSONALIZADA, obraEntity.getTipoMaoDeObra());
        assertEquals(StatusObra.CONCLUIDA, obraEntity.getStatus());
        assertEquals(grupoDeObra, obraEntity.getGrupo());
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissaoDeCriarOuEditar() {
        UserTenantEntity userTenant = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .build();
        userTenant.setUserObraPermitidaEntities(
                Set.of(
                        UserObraPermitidaEntity.builder()
                                .userTenant(userTenant)
                                .obraId(obraId)
                                .build()
                )
        );
        userTenant.setAuthorities(authorityCannotEdit);

        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> updateObraService.execute(
                        updateObraRequest,
                        obraExternalId,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(obraRepository, never()).save(any(ObraEntity.class));
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemObraPermitida() {
        Long obraIdNaoPermitida = 999L;

        UserTenantEntity userTenant = UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .build();
        userTenant.setUserObraPermitidaEntities(
                Set.of(
                        UserObraPermitidaEntity.builder()
                                .userTenant(userTenant)
                                .obraId(obraIdNaoPermitida) // ID diferente da obra que está sendo editada
                                .build()
                )
        );
        userTenant.setAuthorities(authorityCanEdit);

        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> updateObraService.execute(
                        updateObraRequest,
                        obraExternalId,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(obraRepository, never()).save(any(ObraEntity.class));
    }
}

