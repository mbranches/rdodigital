package com.branches.usertenant.service;

import com.branches.exception.ForbiddenException;
import com.branches.exception.NotFoundException;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.TenantRepository;
import com.branches.tenant.repository.projection.TenantInfoProjection;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.repository.UserRepository;
import com.branches.user.repository.projection.UserInfoProjection;
import com.branches.usertenant.domain.UserTenantAuthorities;
import com.branches.usertenant.domain.enums.PerfilUserTenant;
import com.branches.usertenant.dto.response.UserTenantInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserTenantInfoServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private GetUserTenantInfoService getUserTenantInfoService;

    private String tenantExternalId;
    private Long tenantId;
    private Long userId;
    private List<Long> tenantIds;
    private UserInfoProjection userInfoProjection;
    private TenantInfoProjection tenantInfoProjection;
    private List<TenantEntity> tenantEntities;
    TenantEntity tenant;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        userId = 100L;
        tenantIds = List.of(1L);

        tenantInfoProjection = mock(TenantInfoProjection.class);
        userInfoProjection = mock(UserInfoProjection.class);

        tenant = TenantEntity.builder()
                .id(1L)
                .idExterno("tenant-ext-1")
                .razaoSocial("Tenant 1 LTDA")
                .nomeFantasia("Tenant 1")
                .build();

        tenantEntities = List.of(tenant);
    }

    @Test
    void deveRetornarUserTenantInfoResponseQuandoSucesso() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(userRepository.findUserInfoByIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(userInfoProjection));
        when(tenantRepository.findAllByIdInAndAtivoIsTrue(tenantIds)).thenReturn(tenantEntities);
        when(tenantRepository.findTenantInfoById(tenantId))
                .thenReturn(Optional.of(tenantInfoProjection));

        when(userInfoProjection.getIdExterno()).thenReturn("user-ext-100");
        when(userInfoProjection.getNome()).thenReturn("João Silva");
        when(userInfoProjection.getEmail()).thenReturn("joao.silva@email.com");
        when(userInfoProjection.getCargo()).thenReturn("Engenheiro");
        when(userInfoProjection.getFotoUrl()).thenReturn("https://example.com/foto.jpg");
        when(userInfoProjection.getAuthorities()).thenReturn(new UserTenantAuthorities());
        when(userInfoProjection.getPerfil()).thenReturn(PerfilUserTenant.ADMINISTRADOR);

        when(tenantInfoProjection.getIdExterno()).thenReturn(tenantExternalId);
        when(tenantInfoProjection.getRazaoSocial()).thenReturn("Empresa Teste LTDA");
        when(tenantInfoProjection.getNomeFantasia()).thenReturn("Empresa Teste");
        when(tenantInfoProjection.getCnpj()).thenReturn("12.345.678/0001-99");
        when(tenantInfoProjection.getTelefone()).thenReturn("(11) 1234-5678");
        when(tenantInfoProjection.getLogoUrl()).thenReturn("https://example.com/logo.jpg");
        when(tenantInfoProjection.getNomeUsuarioResponsavel()).thenReturn("João Silva");
        when(tenantInfoProjection.getAssinaturaAtiva()).thenReturn(null);
        when(tenantInfoProjection.getQuantidadeDeUsersCriados()).thenReturn(5L);
        when(tenantInfoProjection.getQuantidadeDeObrasCriadas()).thenReturn(10L);

        UserTenantInfoResponse result = getUserTenantInfoService.execute(tenantExternalId, userId, tenantIds);

        assertNotNull(result);

        assertNotNull(result.user());
        assertEquals("user-ext-100", result.user().id());
        assertEquals("João Silva", result.user().nome());
        assertEquals("joao.silva@email.com", result.user().email());
        assertEquals("Engenheiro", result.user().cargo());
        assertEquals("https://example.com/foto.jpg", result.user().fotoUrl());
        assertNotNull(result.user().authorities());
        assertEquals(PerfilUserTenant.ADMINISTRADOR, result.user().perfil());
        assertEquals(1, result.user().tenantsVinculados().size());
        assertEquals("tenant-ext-1", result.user().tenantsVinculados().getFirst().id());
        assertEquals("Tenant 1", result.user().tenantsVinculados().getFirst().nomeFantasia());

        assertNotNull(result.tenant());
        assertEquals(tenantExternalId, result.tenant().id());
        assertEquals("Empresa Teste LTDA", result.tenant().razonSocial());
        assertEquals("Empresa Teste", result.tenant().nomeFantasia());
        assertEquals("12.345.678/0001-99", result.tenant().cnpj());
        assertEquals("(11) 1234-5678", result.tenant().telefone());
        assertEquals("https://example.com/logo.jpg", result.tenant().logoUrl());
        assertEquals("João Silva", result.tenant().responsavelNome());
        assertNull(result.tenant().assinaturaAtiva());
        assertEquals(5L, result.tenant().quantidadeDeUsersCriados());
        assertEquals(10L, result.tenant().quantidadeDeObrasCriadas());
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoPertenceAoTenant() {
        Long tenantIdNaoPertencente = 999L;
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantIdNaoPertencente);

        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->
            getUserTenantInfoService.execute(tenantExternalId, userId, tenantIds)
        );

        assertNotNull(exception);
    }

    @Test
    void deveLancarNotFoundExceptionQuandoUsuarioNaoEncontrado() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(userRepository.findUserInfoByIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            getUserTenantInfoService.execute(tenantExternalId, userId, tenantIds)
        );

        assertNotNull(exception);
        assertEquals("Usuário não encontrado para o tenant informado", exception.getReason());
    }

    @Test
    void deveLancarNotFoundExceptionQuandoTenantNaoEncontrado() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(userRepository.findUserInfoByIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(userInfoProjection));
        when(tenantRepository.findAllByIdInAndAtivoIsTrue(tenantIds)).thenReturn(tenantEntities);
        when(tenantRepository.findTenantInfoById(tenantId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> getUserTenantInfoService.execute(tenantExternalId, userId, tenantIds));

        assertNotNull(exception);
        assertEquals("Tenant não encontrado", exception.getReason());
    }
}
