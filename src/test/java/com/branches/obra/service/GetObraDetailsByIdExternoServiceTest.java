package com.branches.obra.service;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.enums.StatusObra;
import com.branches.obra.domain.enums.TipoContratoDeObra;
import com.branches.obra.dto.response.GetObraDetailsByIdExternoResponse;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.exception.ForbiddenException;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetObraDetailsByIdExternoServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @InjectMocks
    private GetObraDetailsByIdExternoService service;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetObraByIdExternoAndTenantIdService getObraByIdExternoAndTenantIdService;

    private String obraIdExterno;
    private String tenantExternalId;
    private Long tenantId;
    private ObraEntity obraEntity;
    private List<UserTenantEntity> userTenants;

    @BeforeEach
    void setUp() {
        obraIdExterno = "obra-ext-123";
        tenantExternalId = "tenant-ext-123";

        tenantId = 1L;

        obraEntity = ObraEntity.builder()
                .id(1L)
                .idExterno(obraIdExterno)
                .nome("Obra Teste")
                .responsavel("João Silva")
                .contratante("Contratante Teste")
                .tipoContrato(TipoContratoDeObra.CONTRATADA)
                .dataInicio(LocalDate.of(2025, 1, 1))
                .dataPrevistaFim(LocalDate.of(2025, 12, 31))
                .numeroContrato("CONT-2025-001")
                .endereco("Rua Teste, 123")
                .observacoes("Observações de teste")
                .capaUrl("http://capa.url")
                .tipoMaoDeObra(TipoMaoDeObra.PERSONALIZADA)
                .status(StatusObra.EM_ANDAMENTO)
                .ativo(true)
                .build();
        obraEntity.setTenantId(1L);
    }

    @Test
    void deveRetornarObraQuandoUsuarioTiverPerfilAdministrador() {
        UserTenantEntity userTenant = UserTenantEntity.builder()
                .id(UserTenantKey.from(1L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(1L).build())
                .perfil(PerfilUserTenant.ADMINISTRADOR)
                .build();

        userTenant.setUserObraPermitidaEntities(Set.of(new UserObraPermitidaEntity(UserObraPermitidaKey.from(userTenant, obraEntity.getId()), userTenant, obraEntity.getId())));

        userTenants = List.of(
                userTenant
        );

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getObraByIdExternoAndTenantIdService.execute(obraIdExterno, tenantId)).thenReturn(obraEntity);

        GetObraDetailsByIdExternoResponse response = service.execute(
                obraIdExterno,
                tenantExternalId,
                userTenants
        );

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
    }

    @Test
    void deveRetornarObraQuandoUsuarioTemPermissaoNaObra() {
        UserTenantEntity userTenant = UserTenantEntity.builder()
                .id(UserTenantKey.from(1L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(1L).build())
                .perfil(PerfilUserTenant.CLIENTE_OBRA)
                .build();

        userTenant.setUserObraPermitidaEntities(Set.of(new UserObraPermitidaEntity(UserObraPermitidaKey.from(userTenant, obraEntity.getId()), userTenant, obraEntity.getId())));

        userTenants = List.of(
                userTenant
        );

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getObraByIdExternoAndTenantIdService.execute(obraIdExterno, tenantId)).thenReturn(obraEntity);

        GetObraDetailsByIdExternoResponse response = service.execute(
                obraIdExterno,
                tenantExternalId,
                userTenants
        );

        assertNotNull(response);
        assertEquals(obraIdExterno, response.idExterno());
        assertEquals("Obra Teste", response.nome());
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissaoNaObra() {
        UserTenantEntity userTenant = UserTenantEntity.builder()
                .id(UserTenantKey.from(1L, tenantId))
                .tenantId(tenantId)
                .user(UserEntity.builder().id(1L).build())
                .perfil(PerfilUserTenant.CLIENTE_OBRA)
                .build();

        userTenant.setUserObraPermitidaEntities(Set.of()); // Sem permissões para obras

        userTenants = List.of(
                userTenant
        );

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenant);
        when(getObraByIdExternoAndTenantIdService.execute(obraIdExterno, tenantId)).thenReturn(obraEntity);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> service.execute(
                        obraIdExterno,
                        tenantExternalId,
                        userTenants
                )
        );

        assertNotNull(exception);
    }
}

