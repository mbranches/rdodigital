package com.branches.obra.service;

import com.branches.assinatura.domain.AssinaturaEntity;
import com.branches.assinatura.domain.enums.AssinaturaStatus;
import com.branches.assinatura.service.GetAssinaturaActiveByTenantIdService;
import com.branches.configuradores.domain.ModeloDeRelatorioEntity;
import com.branches.configuradores.service.GetModeloDeRelatorioByIdAndTenantIdService;
import com.branches.exception.BadRequestException;
import com.branches.exception.ForbiddenException;
import com.branches.obra.domain.ConfiguracaoRelatoriosEntity;
import com.branches.obra.domain.GrupoDeObraEntity;
import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.obra.dto.request.CreateObraRequest;
import com.branches.obra.dto.response.CreateObraResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.plano.domain.PlanoEntity;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.user.domain.PermissionsDefault;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateObraServiceTest {

    @Mock
    private GetAssinaturaActiveByTenantIdService getAssinaturaActiveByTenantIdService;

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private GetGrupoDeObraByIdAndTenantIdService getGrupoDeObraByIdAndTenantIdService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetTenantByIdExternoService getTenantByIdExternoService;

    @Mock
    private GetModeloDeRelatorioByIdAndTenantIdService getModeloDeRelatorioByIdAndTenantIdService;

    @InjectMocks
    private CreateObraService createObraService;

    private String tenantExternalId;
    private Long tenantId;
    private Long modeloId;
    private Long grupoId;

    private TenantEntity tenant;
    private AssinaturaEntity assinatura;
    private ModeloDeRelatorioEntity modeloDeRelatorio;
    private GrupoDeObraEntity grupo;

    private CreateObraRequest request;
    private List<UserTenantEntity> userTenants;

    private Authorities authorityCanCreate;
    private Authorities authorityCannotCreate;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        modeloId = 1L;
        grupoId = 1L;

        tenant = TenantEntity.builder()
                .id(tenantId)
                .idExterno(tenantExternalId)
                .build();

        PlanoEntity plano = PlanoEntity.builder()
                .id(1L)
                .nome("Plano Premium")
                .descricao("Plano com recursos completos")
                .valor(BigDecimal.valueOf(199.90))
                .limiteObras(10)
                .build();

        assinatura = AssinaturaEntity.builder()
                .id(1L)
                .tenantId(tenantId)
                .plano(plano)
                .status(AssinaturaStatus.ATIVO)
                .dataInicio(LocalDate.of(2025, 1, 1))
                .dataFim(LocalDate.of(2026, 1, 1))
                .build();

        modeloDeRelatorio = ModeloDeRelatorioEntity.builder()
                .id(modeloId)
                .titulo("Modelo Padrão")
                .showMaoDeObra(true)
                .build();
        modeloDeRelatorio.setTenantId(tenantId);

        grupo = GrupoDeObraEntity.builder()
                .id(grupoId)
                .descricao("Grupo de Obras Residenciais")
                .build();
        grupo.setTenantId(tenantId);

        authorityCanCreate = Authorities.builder()
                .obras(PermissionsDefault.builder()
                        .canCreateAndEdit(true)
                        .build())
                .build();

        authorityCannotCreate = Authorities.builder()
                .obras(PermissionsDefault.builder()
                        .canCreateAndEdit(false)
                        .build())
                .build();

        request = new CreateObraRequest(
                "Obra Teste",
                "João Silva",
                "Contratante ABC",
                TipoContratoDeObra.CONTRATADA,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "CONT-2025-001",
                "Rua Teste, 123",
                "Observações de teste",
                TipoMaoDeObra.PERSONALIZADA,
                StatusObra.EM_ANDAMENTO,
                1L,
                modeloId
        );
    }

    @Test
    void deveCriarObraComSucesso() {
        UserTenantEntity userTenant = createUserTenant(authorityCanCreate);
        userTenants = List.of(userTenant);

        ObraEntity obraSalva = ObraEntity.builder()
                .id(1L)
                .idExterno("obra-ext-001")
                .nome(request.nome())
                .responsavel(request.responsavel())
                .contratante(request.contratante())
                .tipoContrato(request.tipoContrato())
                .dataInicio(request.dataInicio())
                .dataPrevistaFim(request.dataPrevistaFim())
                .numeroContrato(request.numeroContrato())
                .endereco(request.endereco())
                .observacoes(request.observacoes())
                .tipoMaoDeObra(request.tipoMaoDeObra())
                .status(request.status())
                .configuracaoRelatorios(ConfiguracaoRelatoriosEntity.by(modeloDeRelatorio))
                .ativo(true)
                .build();
        obraSalva.setTenantId(tenantId);

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenant);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getAssinaturaActiveByTenantIdService.execute(tenantId)).thenReturn(assinatura);
        when(obraRepository.countByTenantIdAndAtivoIsTrue(tenantId)).thenReturn(5);
        when(getModeloDeRelatorioByIdAndTenantIdService.execute(modeloId, tenantId))
                .thenReturn(modeloDeRelatorio);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraSalva);

        CreateObraResponse response = createObraService.execute(request, tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals("obra-ext-001", response.id());
        assertEquals("Obra Teste", response.nome());
        assertEquals("João Silva", response.responsavel());
        assertEquals("Contratante ABC", response.contratante());
        assertEquals(StatusObra.EM_ANDAMENTO, response.status());

        verify(getTenantByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(getAssinaturaActiveByTenantIdService, times(1)).execute(tenantId);
        verify(obraRepository, times(1)).countByTenantIdAndAtivoIsTrue(tenantId);
        verify(getModeloDeRelatorioByIdAndTenantIdService, times(1)).execute(modeloId, tenantId);
        verify(obraRepository, times(1)).save(any(ObraEntity.class));
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissaoParaCriar() {
        UserTenantEntity userTenant = createUserTenant(authorityCannotCreate);
        userTenants = List.of(userTenant);

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenant);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> createObraService.execute(request, tenantExternalId, userTenants)
        );

        assertNotNull(exception);
        verify(getTenantByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(obraRepository, never()).save(any(ObraEntity.class));
    }

    @Test
    void deveLancarBadRequestExceptionQuandoLimiteDeObrasAtingido() {
        UserTenantEntity userTenant = createUserTenant(authorityCanCreate);
        userTenants = List.of(userTenant);

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenant);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getAssinaturaActiveByTenantIdService.execute(tenantId)).thenReturn(assinatura);
        when(obraRepository.countByTenantIdAndAtivoIsTrue(tenantId)).thenReturn(10);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> createObraService.execute(request, tenantExternalId, userTenants)
        );

        assertEquals("Limite de obras atingido", exception.getReason());
        verify(getTenantByIdExternoService, times(1)).execute(tenantExternalId);
        verify(getCurrentUserTenantService, times(1)).execute(userTenants, tenantId);
        verify(getAssinaturaActiveByTenantIdService, times(1)).execute(tenantId);
        verify(obraRepository, times(1)).countByTenantIdAndAtivoIsTrue(tenantId);
        verify(obraRepository, never()).save(any(ObraEntity.class));
    }

    @Test
    void deveCriarObraComGrupoQuandoGrupoIdFornecido() {
        CreateObraRequest requestComGrupo = createRequestComGrupo(grupoId);
        UserTenantEntity userTenant = createUserTenant(authorityCanCreate);
        userTenants = List.of(userTenant);

        ObraEntity obraSalva = ObraEntity.builder()
                .id(1L)
                .idExterno("obra-ext-002")
                .nome(requestComGrupo.nome())
                .grupo(grupo)
                .ativo(true)
                .build();
        obraSalva.setTenantId(tenantId);

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenant);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getAssinaturaActiveByTenantIdService.execute(tenantId)).thenReturn(assinatura);
        when(obraRepository.countByTenantIdAndAtivoIsTrue(tenantId)).thenReturn(3);
        when(getGrupoDeObraByIdAndTenantIdService.execute(grupoId, tenantId)).thenReturn(grupo);
        when(getModeloDeRelatorioByIdAndTenantIdService.execute(modeloId, tenantId))
                .thenReturn(modeloDeRelatorio);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraSalva);

        CreateObraResponse response = createObraService.execute(requestComGrupo, tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals("obra-ext-002", response.id());
        verify(getGrupoDeObraByIdAndTenantIdService, times(1)).execute(grupoId, tenantId);
        verify(obraRepository, times(1)).save(argThat(obra ->
                obra.getGrupo() != null && obra.getGrupo().getId().equals(grupoId)
        ));
    }

    @Test
    void deveDefinirDataFimRealQuandoObraForCriadaComStatusConcluida() {
        CreateObraRequest requestConcluida = createRequestConcluida();
        UserTenantEntity userTenant = createUserTenant(authorityCanCreate);
        userTenants = List.of(userTenant);

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenant);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getAssinaturaActiveByTenantIdService.execute(tenantId)).thenReturn(assinatura);
        when(obraRepository.countByTenantIdAndAtivoIsTrue(tenantId)).thenReturn(2);
        when(getModeloDeRelatorioByIdAndTenantIdService.execute(modeloId, tenantId))
                .thenReturn(modeloDeRelatorio);
        when(obraRepository.save(any(ObraEntity.class))).thenAnswer(invocation -> {
            ObraEntity obra = invocation.getArgument(0);
            obra.setId(1L);
            obra.setIdExterno("obra-ext-003");
            return obra;
        });

        CreateObraResponse response = createObraService.execute(requestConcluida, tenantExternalId, userTenants);

        assertNotNull(response);
        verify(obraRepository, times(1)).save(argThat(obra ->
                obra.getStatus() == StatusObra.CONCLUIDA &&
                        obra.getDataFimReal() != null &&
                        obra.getDataFimReal().equals(LocalDate.now())
        ));
    }

    @Test
    void deveCriarObraSemGrupoQuandoGrupoIdForNull() {
        UserTenantEntity userTenant = createUserTenant(authorityCanCreate);
        userTenants = List.of(userTenant);

        ObraEntity obraSalva = ObraEntity.builder()
                .id(1L)
                .idExterno("obra-ext-004")
                .nome(request.nome())
                .grupo(null)
                .ativo(true)
                .build();
        obraSalva.setTenantId(tenantId);

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenant);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getAssinaturaActiveByTenantIdService.execute(tenantId)).thenReturn(assinatura);
        when(obraRepository.countByTenantIdAndAtivoIsTrue(tenantId)).thenReturn(1);
        when(getModeloDeRelatorioByIdAndTenantIdService.execute(modeloId, tenantId))
                .thenReturn(modeloDeRelatorio);
        when(obraRepository.save(any(ObraEntity.class))).thenReturn(obraSalva);

        CreateObraResponse response = createObraService.execute(request.withGrupoId(null), tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals("obra-ext-004", response.id());
        verify(getGrupoDeObraByIdAndTenantIdService, never()).execute(anyLong(), anyLong());
        verify(obraRepository, times(1)).save(argThat(obra -> obra.getGrupo() == null));
    }

    private UserTenantEntity createUserTenant(Authorities authorities) {
        return UserTenantEntity.builder()
                .user(UserEntity.builder().id(1L).build())
                .tenantId(tenantId)
                .authorities(authorities)
                .build();
    }

    private CreateObraRequest createRequestComGrupo(Long grupoId) {
        return new CreateObraRequest(
                request.nome(),
                request.responsavel(),
                request.contratante(),
                request.tipoContrato(),
                request.dataInicio(),
                request.dataPrevistaFim(),
                request.numeroContrato(),
                request.endereco(),
                request.observacoes(),
                request.tipoMaoDeObra(),
                request.status(),
                grupoId,
                modeloId
        );
    }

    private CreateObraRequest createRequestConcluida() {
        return new CreateObraRequest(
                "Obra Concluída",
                request.responsavel(),
                request.contratante(),
                request.tipoContrato(),
                LocalDate.now().minusMonths(6),
                LocalDate.now(),
                request.numeroContrato(),
                request.endereco(),
                request.observacoes(),
                request.tipoMaoDeObra(),
                StatusObra.CONCLUIDA,
                1L,
                modeloId
        );
    }
}

