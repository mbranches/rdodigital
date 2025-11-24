package com.branches.obra.service;

import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.dto.request.UpdateObraRequest;
import com.branches.obra.repository.ObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.PermissionsDefault;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.Authorities;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
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

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;

    @InjectMocks
    private UpdateObraService updateObraService;

    private UpdateObraRequest updateObraRequest;
    private ObraEntity obraEntity;
    private GrupoDeObraEntity grupoDeObra;
    private String obraExternalId;
    private String tenantExternalId;
    private Long tenantId;
    private Long obraId;
    private Long grupoId;
    private List<UserTenantEntity> userTenants;
    private Authorities authorityCanEdit;
    private Authorities authorityCannotEdit;

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

        authorityCanEdit = Authorities.builder()
                .obras(
                        PermissionsDefault.builder()
                                .canCreateAndEdit(true)
                                .build()
                )
                .build();

        authorityCannotEdit = Authorities.builder()
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
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(getObraByIdExternoAndTenantIdService, times(1)).execute(obraExternalId, tenantId);
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
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(getObraByIdExternoAndTenantIdService, times(1)).execute(obraExternalId, tenantId);
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
                                .obraId(obraIdNaoPermitida)
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
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(getObraByIdExternoAndTenantIdService, times(1)).execute(obraExternalId, tenantId);
        verify(obraRepository, never()).save(any(ObraEntity.class));
    }

    @Test
    void deveDefinirDataFimRealQuandoStatusMudarParaConcluida() {
        UpdateObraRequest requestConcluida = new UpdateObraRequest(
                "Obra Concluída",
                "Maria Silva",
                "Contratante",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "CONT-2025-001",
                "Endereço",
                "Observações",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.CONCLUIDA,
                grupoId
        );

        UserTenantEntity userTenant = createUserTenantWithPermission(obraId);
        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId))
                .thenReturn(grupoDeObra);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraEntity);

        updateObraService.execute(requestConcluida, obraExternalId, tenantExternalId, userTenants);

        assertEquals(StatusObra.CONCLUIDA, obraEntity.getStatus());
        assertNotNull(obraEntity.getDataFimReal());
        assertEquals(LocalDate.now(), obraEntity.getDataFimReal());

        verify(obraRepository, times(1)).save(obraEntity);
    }

    @Test
    void deveRemoverDataFimRealQuandoStatusMudarDeConcluidaParaOutro() {
        obraEntity.setStatus(StatusObra.CONCLUIDA);
        obraEntity.setDataFimReal(LocalDate.of(2025, 10, 1));

        UpdateObraRequest requestEmAndamento = new UpdateObraRequest(
                "Obra Em Andamento Novamente",
                "Maria Silva",
                "Contratante",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "CONT-2025-001",
                "Endereço",
                "Observações",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.EM_ANDAMENTO,
                grupoId
        );

        UserTenantEntity userTenant = createUserTenantWithPermission(obraId);
        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId))
                .thenReturn(grupoDeObra);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraEntity);

        updateObraService.execute(requestEmAndamento, obraExternalId, tenantExternalId, userTenants);

        assertEquals(StatusObra.EM_ANDAMENTO, obraEntity.getStatus());
        assertNull(obraEntity.getDataFimReal());

        verify(obraRepository, times(1)).save(obraEntity);
    }

    @Test
    void deveManterDataFimRealNullQuandoStatusJaEraConcluida() {
        obraEntity.setStatus(StatusObra.CONCLUIDA);
        LocalDate oldDataFimReal = LocalDate.of(2025, 10, 1);
        obraEntity.setDataFimReal(oldDataFimReal);

        UpdateObraRequest requestConcluida = new UpdateObraRequest(
                "Obra Concluída",
                "Maria Silva",
                "Contratante",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "CONT-2025-001",
                "Endereço",
                "Observações",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.CONCLUIDA,
                grupoId
        );

        UserTenantEntity userTenant = createUserTenantWithPermission(obraId);
        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId))
                .thenReturn(grupoDeObra);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraEntity);

        updateObraService.execute(requestConcluida, obraExternalId, tenantExternalId, userTenants);

        assertEquals(StatusObra.CONCLUIDA, obraEntity.getStatus());
        assertEquals(oldDataFimReal, obraEntity.getDataFimReal());

        verify(obraRepository, times(1)).save(obraEntity);
    }

    @Test
    void deveAtualizarObraSemGrupoQuandoGrupoIdForNull() {
        UpdateObraRequest requestSemGrupo = new UpdateObraRequest(
                "Obra Sem Grupo",
                "Maria Silva",
                "Contratante",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "CONT-2025-001",
                "Endereço",
                "Observações",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.EM_ANDAMENTO,
                null
        );

        UserTenantEntity userTenant = createUserTenantWithPermission(obraId);
        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraEntity);

        updateObraService.execute(requestSemGrupo, obraExternalId, tenantExternalId, userTenants);

        assertEquals("Obra Sem Grupo", obraEntity.getNome());
        assertNull(obraEntity.getGrupo());

        verify(getGrupoDeObraByIdAndTenantIdService, never()).execute(null, tenantId);
        verify(obraRepository, times(1)).save(obraEntity);
    }

    @Test
    void deveAtualizarTodosOsCamposCorretamente() {
        UserTenantEntity userTenant = createUserTenantWithPermission(obraId);
        userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getObraByIdExternoAndTenantIdService.execute(obraExternalId, tenantId))
                .thenReturn(obraEntity);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId))
                .thenReturn(grupoDeObra);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraEntity);

        updateObraService.execute(updateObraRequest, obraExternalId, tenantExternalId, userTenants);

        assertEquals(updateObraRequest.nome(), obraEntity.getNome());
        assertEquals(updateObraRequest.responsavel(), obraEntity.getResponsavel());
        assertEquals(updateObraRequest.contratante(), obraEntity.getContratante());
        assertEquals(updateObraRequest.tipoContrato(), obraEntity.getTipoContrato());
        assertEquals(updateObraRequest.dataInicio(), obraEntity.getDataInicio());
        assertEquals(updateObraRequest.dataPrevistaFim(), obraEntity.getDataPrevistaFim());
        assertEquals(updateObraRequest.numeroContrato(), obraEntity.getNumeroContrato());
        assertEquals(updateObraRequest.endereco(), obraEntity.getEndereco());
        assertEquals(updateObraRequest.observacoes(), obraEntity.getObservacoes());
        assertEquals(updateObraRequest.tipoMaoDeObra(), obraEntity.getTipoMaoDeObra());
        assertEquals(updateObraRequest.status(), obraEntity.getStatus());

        verify(obraRepository, times(1)).save(obraEntity);
    }

    private UserTenantEntity createUserTenantWithPermission(Long obraId) {
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
        return userTenant;
    }
}

