package com.branches.obra.service;

import com.branches.exception.ForbiddenException;
import com.branches.exception.NotFoundException;
import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.dto.response.GetObraDetailsByIdExternoResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.obra.repository.projections.ObraDetailsProjection;
import com.branches.relatorio.repository.RelatorioRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.*;
import com.branches.usertenant.domain.*;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetObraDetailsByIdExternoServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private RelatorioRepository relatorioRepository;

    @Mock
    private ObraRepository obraRepository;

    @InjectMocks
    private GetObraDetailsByIdExternoService service;

    private String obraIdExterno;
    private String tenantExternalId;
    private Long tenantId;
    private Long obraId;

    @BeforeEach
    void setUp() {
        obraIdExterno = "obra-ext-123";
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        obraId = 1L;
    }

    private ObraDetailsProjection createObraDetailsProjection(
            Long id,
            String idExterno,
            String nome,
            String responsavel,
            String contratante,
            TipoContratoDeObra tipoContrato,
            LocalDate dataInicio,
            LocalDate dataPrevistaFim,
            LocalDate dataFimReal,
            String numeroContrato,
            String endereco,
            String observacoes,
            String capaUrl,
            TipoMaoDeObra tipoMaoDeObra,
            StatusObra status,
            Long quantidadeRelatorios,
            Long quantidadeAtividades,
            Long quantidadeOcorrencias,
            Long quantidadeComentarios
    ) {
        return new ObraDetailsProjection() {
            @Override
            public Long getId() {
                return id;
            }

            @Override
            public String getIdExterno() {
                return idExterno;
            }

            @Override
            public String getNome() {
                return nome;
            }

            @Override
            public String getResponsavel() {
                return responsavel;
            }

            @Override
            public String getContratante() {
                return contratante;
            }

            @Override
            public TipoContratoDeObra getTipoContrato() {
                return tipoContrato;
            }

            @Override
            public LocalDate getDataInicio() {
                return dataInicio;
            }

            @Override
            public LocalDate getDataPrevistaFim() {
                return dataPrevistaFim;
            }

            @Override
            public LocalDate getDataFimReal() {
                return dataFimReal;
            }

            @Override
            public String getNumeroContrato() {
                return numeroContrato;
            }

            @Override
            public String getEndereco() {
                return endereco;
            }

            @Override
            public String getObservacoes() {
                return observacoes;
            }

            @Override
            public String getCapaUrl() {
                return capaUrl;
            }

            @Override
            public StatusObra getStatus() {
                return status;
            }

            @Override
            public TipoMaoDeObra getTipoMaoDeObra() {
                return tipoMaoDeObra;
            }

            @Override
            public GrupoDeObraEntity getGrupoDeObra() {
                return null;
            }

            @Override
            public Long getQuantidadeRelatorios() {
                return quantidadeRelatorios;
            }

            @Override
            public Long getQuantidadeAtividades() {
                return quantidadeAtividades;
            }

            @Override
            public Long getQuantidadeOcorrencias() {
                return quantidadeOcorrencias;
            }

            @Override
            public Long getQuantidadeComentarios() {
                return quantidadeComentarios;
            }
        };
    }

    @Test
    void deveRetornarObraQuandoUsuarioTiverPerfilAdministrador() {
        // Arrange
        ObraDetailsProjection obraDetailsProjection = createObraDetailsProjection(
                obraId,
                obraIdExterno,
                "Obra Teste",
                "João Silva",
                "Contratante Teste",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                null,
                "CONT-2025-001",
                "Rua Teste, 123",
                "Observações de teste",
                "http://capa.url",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.EM_ANDAMENTO,
                5L,
                10L,
                2L,
                3L
        );

        UserTenantEntity userTenant = UserTenantEntity.builder()
                .id(UserTenantKey.from(1L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(1L).build())
                .perfil(PerfilUserTenant.ADMINISTRADOR)
                .build();

        userTenant.setUserObraPermitidaEntities(Set.of(
                new UserObraPermitidaEntity(UserObraPermitidaKey.from(userTenant, obraId), userTenant, obraId)
        ));

        List<UserTenantEntity> userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(obraRepository.findObraDetailsByIdExternoAndTenantId(obraIdExterno, tenantId))
                .thenReturn(Optional.of(obraDetailsProjection));
        when(relatorioRepository.findTop5ByObraIdProjection(obraId)).thenReturn(List.of());

        // Act
        GetObraDetailsByIdExternoResponse response = service.execute(
                obraIdExterno,
                tenantExternalId,
                userTenants
        );

        // Assert
        assertNotNull(response);
        assertEquals(obraIdExterno, response.idExterno());
        assertEquals("Obra Teste", response.nome());
        assertEquals("João Silva", response.responsavel());
        assertEquals("Contratante Teste", response.contratante());
        assertEquals(TipoContratoDeObra.CONTRATADA, response.tipoContrato());
        assertEquals(StatusObra.EM_ANDAMENTO, response.status());
        assertEquals(LocalDate.of(2025, 1, 1), response.dataInicio());
        assertEquals(LocalDate.of(2025, 12, 31), response.dataPrevistaFim());
        assertEquals("CONT-2025-001", response.numeroContrato());
        assertEquals("Rua Teste, 123", response.endereco());
        assertEquals("Observações de teste", response.observacoes());
        assertEquals(TipoMaoDeObra.PERSONALIZADA, response.tipoMaoDeObra());

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(obraRepository, times(1)).findObraDetailsByIdExternoAndTenantId(obraIdExterno, tenantId);
        verify(relatorioRepository, times(1)).findTop5ByObraIdProjection(obraId);
    }

    @Test
    void deveRetornarObraQuandoUsuarioTemPermissaoNaObra() {
        // Arrange
        ObraDetailsProjection obraDetailsProjection = createObraDetailsProjection(
                obraId,
                obraIdExterno,
                "Obra Teste",
                "João Silva",
                "Contratante Teste",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                null,
                "CONT-2025-001",
                "Rua Teste, 123",
                "Observações de teste",
                "http://capa.url",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.EM_ANDAMENTO,
                5L,
                10L,
                2L,
                3L
        );

        UserTenantEntity userTenant = UserTenantEntity.builder()
                .id(UserTenantKey.from(1L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(1L).build())
                .perfil(PerfilUserTenant.CLIENTE_OBRA)
                .build();

        userTenant.setUserObraPermitidaEntities(Set.of(
                new UserObraPermitidaEntity(UserObraPermitidaKey.from(userTenant, obraId), userTenant, obraId)
        ));

        List<UserTenantEntity> userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(obraRepository.findObraDetailsByIdExternoAndTenantId(obraIdExterno, tenantId))
                .thenReturn(Optional.of(obraDetailsProjection));
        when(relatorioRepository.findTop5ByObraIdProjection(obraId)).thenReturn(List.of());

        // Act
        GetObraDetailsByIdExternoResponse response = service.execute(
                obraIdExterno,
                tenantExternalId,
                userTenants
        );

        // Assert
        assertNotNull(response);
        assertEquals(obraIdExterno, response.idExterno());
        assertEquals("Obra Teste", response.nome());

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(obraRepository, times(1)).findObraDetailsByIdExternoAndTenantId(obraIdExterno, tenantId);
        verify(relatorioRepository, times(1)).findTop5ByObraIdProjection(obraId);
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissaoNaObra() {
        // Arrange
        ObraDetailsProjection obraDetailsProjection = createObraDetailsProjection(
                obraId,
                obraIdExterno,
                "Obra Teste",
                "João Silva",
                "Contratante Teste",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                null,
                "CONT-2025-001",
                "Rua Teste, 123",
                "Observações de teste",
                "http://capa.url",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.EM_ANDAMENTO,
                5L,
                10L,
                2L,
                3L
        );

        UserTenantEntity userTenant = UserTenantEntity.builder()
                .id(UserTenantKey.from(1L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(1L).build())
                .perfil(PerfilUserTenant.CLIENTE_OBRA)
                .build();

        userTenant.setUserObraPermitidaEntities(Set.of());

        List<UserTenantEntity> userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(obraRepository.findObraDetailsByIdExternoAndTenantId(obraIdExterno, tenantId))
                .thenReturn(Optional.of(obraDetailsProjection));
        when(relatorioRepository.findTop5ByObraIdProjection(obraId)).thenReturn(List.of());

        // Act & Assert
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> service.execute(
                        obraIdExterno,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(obraRepository, times(1)).findObraDetailsByIdExternoAndTenantId(obraIdExterno, tenantId);
        verify(relatorioRepository, times(1)).findTop5ByObraIdProjection(obraId);
    }

    @Test
    void deveLancarNotFoundExceptionQuandoObraNaoEncontrada() {
        // Arrange
        UserTenantEntity userTenant = UserTenantEntity.builder()
                .id(UserTenantKey.from(1L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(1L).build())
                .perfil(PerfilUserTenant.ADMINISTRADOR)
                .build();

        List<UserTenantEntity> userTenants = List.of(userTenant);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(obraRepository.findObraDetailsByIdExternoAndTenantId(obraIdExterno, tenantId))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.execute(
                        obraIdExterno,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);

        verify(getTenantIdByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(obraRepository, times(1)).findObraDetailsByIdExternoAndTenantId(obraIdExterno, tenantId);
        verify(relatorioRepository, never()).findTop5ByObraIdProjection(anyLong());
    }
}

