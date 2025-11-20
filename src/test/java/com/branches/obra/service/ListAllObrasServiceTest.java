package com.branches.obra.service;

import com.branches.obra.domain.ObraEntity;
import com.branches.obra.domain.StatusObra;
import com.branches.obra.domain.TipoContratoDeObra;
import com.branches.obra.dto.response.ObraByListAllResponse;
import com.branches.obra.repository.ObraRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.domain.UserTenantKey;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.Authorities;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListAllObrasServiceTest {

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @InjectMocks
    private ListAllObrasService listAllObrasService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    private String tenantExternalId;
    private Long tenantId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantAdministrador;
    private UserTenantEntity userTenantPersonalizado;
    private List<ObraEntity> obras;
    private ObraEntity obra1;
    private ObraEntity obra2;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;

        userTenantAdministrador = UserTenantEntity.builder()
                .id(UserTenantKey.from(100L, tenantId))
                .tenantId(tenantId)
                .perfil(PerfilUserTenant.ADMINISTRADOR)
                .authorities(new Authorities())
                .ativo(true)
                .userObraPermitidaEntities(new HashSet<>())
                .build();

        UserObraPermitidaEntity obraPermitida1 = UserObraPermitidaEntity.builder()
                .obraId(1L)
                .build();

        Set<UserObraPermitidaEntity> obrasPermitidas = new HashSet<>();
        obrasPermitidas.add(obraPermitida1);

        userTenantPersonalizado = UserTenantEntity.builder()
                .id(UserTenantKey.from(101L, tenantId))
                .tenantId(tenantId)
                .perfil(PerfilUserTenant.PERSONALIZADO)
                .authorities(new Authorities())
                .ativo(true)
                .userObraPermitidaEntities(obrasPermitidas)
                .build();

        obra1 = ObraEntity.builder()
                .id(1L)
                .idExterno("obra-ext-1")
                .nome("Obra 1")
                .status(StatusObra.CONCLUIDA)
                .capaUrl("https://example.com/obra1.jpg")
                .responsavel("Maria Santos")
                .contratante("Empresa A")
                .tipoContrato(TipoContratoDeObra.CONTRATADA)
                .dataInicio(LocalDate.now().minusMonths(3))
                .dataPrevistaFim(LocalDate.now())
                .tipoMaoDeObra(TipoMaoDeObra.PERSONALIZADA)
                .ativo(true)
                .build();

        obra2 = ObraEntity.builder()
                .id(2L)
                .idExterno("obra-ext-2")
                .nome("Obra 2")
                .status(StatusObra.EM_ANDAMENTO)
                .capaUrl("https://example.com/obra2.jpg")
                .responsavel("Carlos Pereira")
                .contratante("Empresa B")
                .tipoContrato(TipoContratoDeObra.CONTRATADA)
                .dataInicio(LocalDate.of(2024, 12, 1))
                .dataPrevistaFim(LocalDate.of(2025, 6, 1))
                .tipoMaoDeObra(TipoMaoDeObra.PERSONALIZADA)
                .ativo(true)
                .build();

        obras = List.of(obra1, obra2);
    }

    @Test
    void deveRetornarTodasObrasQuandoUsuarioEhAdministrador() {
        userTenants = List.of(userTenantAdministrador);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
//        when(obraRepository.findAllByTenantId(tenantId)).thenReturn(obras);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantAdministrador);

        List<ObraByListAllResponse> result = listAllObrasService.execute(tenantExternalId, userTenants);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("obra-ext-1", result.getFirst().id());
        assertEquals("Obra 1", result.getFirst().nome());
        assertEquals(StatusObra.CONCLUIDA, result.getFirst().status());
        assertEquals("https://example.com/obra1.jpg", result.getFirst().capaUrl());

        assertEquals("obra-ext-2", result.get(1).id());
        assertEquals("Obra 2", result.get(1).nome());
        assertEquals(StatusObra.EM_ANDAMENTO, result.get(1).status());
        assertEquals("https://example.com/obra2.jpg", result.get(1).capaUrl());
    }

    @Test
    void deveRetornarObrasPermitidasQuandoUsuarioTemPerfilPersonalizado() {
        userTenants = List.of(userTenantPersonalizado);
        List<Long> obrasPermitidasIds = List.of(1L);
        List<ObraEntity> obrasPermitidas = List.of(obra1);

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(obraRepository.findAllByTenantIdAndIdInAndAtivoIsTrue(tenantId, obrasPermitidasIds)).thenReturn(obrasPermitidas);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantPersonalizado);

        List<ObraByListAllResponse> result = listAllObrasService.execute(tenantExternalId, userTenants);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals("obra-ext-1", result.getFirst().id());
        assertEquals("Obra 1", result.getFirst().nome());
        assertEquals(StatusObra.CONCLUIDA, result.getFirst().status());
        assertEquals("https://example.com/obra1.jpg", result.getFirst().capaUrl());
    }
}

