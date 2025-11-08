package com.branches.relatorio.tipodeocorrencia.service;

import com.branches.exception.ForbiddenException;
import com.branches.relatorio.tipodeocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.relatorio.tipodeocorrencia.dto.request.CreateTipoDeOcorrenciaRequest;
import com.branches.relatorio.tipodeocorrencia.dto.response.CreateTipoDeOcorrenciaResponse;
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
class CreateTipoDeOcorrenciaServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    @InjectMocks
    private CreateTipoDeOcorrenciaService createTipoDeOcorrenciaService;

    private String tenantExternalId;
    private Long tenantId;
    private CreateTipoDeOcorrenciaRequest request;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private UserTenantEntity userTenantWithoutAccess;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        request = new CreateTipoDeOcorrenciaRequest("Tipo de Ocorrência Teste");

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
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        TipoDeOcorrenciaEntity savedEntity = TipoDeOcorrenciaEntity.builder()
                .id(1L)
                .descricao("Tipo de Ocorrência Teste")
                .ativo(true)
                .tenantId(tenantId)
                .build();

        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(tipoDeOcorrenciaRepository.save(any(TipoDeOcorrenciaEntity.class))).thenReturn(savedEntity);

        CreateTipoDeOcorrenciaResponse response = createTipoDeOcorrenciaService.execute(
                tenantExternalId,
                request,
                userTenants
        );

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(request.descricao(), response.descricao());
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithoutAccess);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> createTipoDeOcorrenciaService.execute(
                        tenantExternalId,
                        request,
                        userTenants
                )
        );

        assertNotNull(exception);
    }
}

