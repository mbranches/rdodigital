package com.branches.obra.service;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.StatusObra;
import com.branches.obra.domain.TipoContratoDeObra;
import com.branches.obra.dto.response.GetObraDetailsByIdExternoResponse;
import com.branches.obra.port.LoadObraPort;
import com.branches.shared.dto.TenantDto;
import com.branches.shared.dto.UserDto.UserTenantDto;
import com.branches.shared.enums.TipoMaoDeObra;
import com.branches.shared.exception.ForbiddenException;
import com.branches.tenant.service.GetTenantByIdExternoService;
import com.branches.user.domain.enums.PerfilUserTenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetObraDetailsByIdExternoServiceTest {

    @Mock
    private LoadObraPort loadObra;

    @Mock
    private GetTenantByIdExternoService getTenantByIdExternoService;

    @InjectMocks
    private GetObraDetailsByIdExternoService service;

    private String obraIdExterno;
    private String tenantExternalId;
    private TenantDto tenantDto;
    private ObraEntity obraEntity;
    private List<UserTenantDto> userTenants;
    private List<Long> userAllowedObraIds;

    @BeforeEach
    void setUp() {
        obraIdExterno = "obra-ext-123";
        tenantExternalId = "tenant-ext-123";

        tenantDto = new TenantDto(
                1L,
                tenantExternalId,
                "Razão Social Teste",
                "Nome Fantasia Teste",
                "12345678000199",
                "http://logo.url",
                "11999999999",
                true
        );

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
        userTenants = List.of(
                new UserTenantDto(1L, PerfilUserTenant.ADMINISTRADOR, List.of())
        );

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenantDto);
        when(loadObra.getObraByIdExternoAndTenantId(obraIdExterno, tenantDto.id())).thenReturn(obraEntity);

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
        userTenants = List.of(
                new UserTenantDto(1L, PerfilUserTenant.CLIENTE_OBRA, List.of(1L, 2L, 3L))
        );

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenantDto);
        when(loadObra.getObraByIdExternoAndTenantId(obraIdExterno, tenantDto.id())).thenReturn(obraEntity);

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
    void deveLancarForbiddenExceptionQuandoTenantNaoEstaNaListaDoUsuario() {
        userTenants = List.of(
                new UserTenantDto(2L, PerfilUserTenant.ADMINISTRADOR, List.of()),
                new UserTenantDto(3L, PerfilUserTenant.ADMINISTRADOR, List.of())
        );

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenantDto);

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

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissaoNaObra() {
        userTenants = List.of(
                new UserTenantDto(1L, PerfilUserTenant.CLIENTE_OBRA, List.of(2L, 3L, 4L))
        );

        when(getTenantByIdExternoService.execute(tenantExternalId)).thenReturn(tenantDto);
        when(loadObra.getObraByIdExternoAndTenantId(obraIdExterno, tenantDto.id())).thenReturn(obraEntity);

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

