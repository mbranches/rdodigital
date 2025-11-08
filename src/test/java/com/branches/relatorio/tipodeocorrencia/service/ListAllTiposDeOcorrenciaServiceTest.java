package com.branches.relatorio.tipodeocorrencia.service;

import com.branches.exception.ForbiddenException;
import com.branches.relatorio.tipodeocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.relatorio.tipodeocorrencia.dto.response.TipoDeOcorrenciaResponse;
import com.branches.relatorio.tipodeocorrencia.repository.TipoDeOcorrenciaRepository;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.PermissionsCadastro;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserTenantAuthorities;
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
class ListAllTiposDeOcorrenciaServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    @InjectMocks
    private ListAllTiposDeOcorrenciaService listAllTiposDeOcorrenciaService;

    private String tenantExternalId;
    private Long tenantId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private UserTenantEntity userTenantWithoutAccess;
    private List<TipoDeOcorrenciaEntity> tipoDeOcorrenciaEntityList;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;

        UserTenantAuthorities authoritiesWithAccess = UserTenantAuthorities.builder()
                .cadastros(PermissionsCadastro.builder()
                        .tiposDeOcorrencia(true)
                        .build())
                .build();

        UserTenantAuthorities authoritiesWithoutAccess = UserTenantAuthorities.builder()
                .cadastros(PermissionsCadastro.builder()
                        .tiposDeOcorrencia(false)
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

        TipoDeOcorrenciaEntity tipoDeOcorrencia1 = TipoDeOcorrenciaEntity.builder()
                .id(1L)
                .descricao("Tipo de Ocorrência 1")
                .ativo(true)
                .tenantId(tenantId)
                .build();

        TipoDeOcorrenciaEntity tipoDeOcorrencia2 = TipoDeOcorrenciaEntity.builder()
                .id(2L)
                .descricao("Tipo de Ocorrência 2")
                .ativo(true)
                .tenantId(tenantId)
                .build();

        tipoDeOcorrenciaEntityList = List.of(tipoDeOcorrencia1, tipoDeOcorrencia2);
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(tipoDeOcorrenciaRepository.findAllByTenantIdAndAtivoIsTrue(tenantId)).thenReturn(tipoDeOcorrenciaEntityList);

        List<TipoDeOcorrenciaResponse> response = listAllTiposDeOcorrenciaService.execute(tenantExternalId, userTenants);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals("Tipo de Ocorrência 1", response.get(0).descricao());
        assertEquals(2L, response.get(1).id());
        assertEquals("Tipo de Ocorrência 2", response.get(1).descricao());
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithoutAccess);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> listAllTiposDeOcorrenciaService.execute(tenantExternalId, userTenants)
        );

        assertNotNull(exception);
    }
}

