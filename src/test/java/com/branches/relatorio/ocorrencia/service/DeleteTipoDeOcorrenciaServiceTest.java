package com.branches.relatorio.ocorrencia.service;

import com.branches.exception.ForbiddenException;
import com.branches.ocorrencia.domain.TipoDeOcorrenciaEntity;
import com.branches.ocorrencia.repository.TipoDeOcorrenciaRepository;
import com.branches.ocorrencia.service.DeleteTipoDeOcorrenciaService;
import com.branches.ocorrencia.service.GetTipoDeOcorrenciaByIdAndTenantIdService;
import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.PermissionsCadastro;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTipoDeOcorrenciaServiceTest {

    @Mock
    private GetTenantIdByIdExternoService getTenantIdByIdExternoService;

    @Mock
    private GetCurrentUserTenantService getCurrentUserTenantService;

    @Mock
    private GetTipoDeOcorrenciaByIdAndTenantIdService getTipoDeOcorrenciaByIdAndTenantIdService;

    @Mock
    private TipoDeOcorrenciaRepository tipoDeOcorrenciaRepository;

    @InjectMocks
    private DeleteTipoDeOcorrenciaService deleteTipoDeOcorrenciaService;

    private String tenantExternalId;
    private Long tenantId;
    private Long tipoDeOcorrenciaId;
    private List<UserTenantEntity> userTenants;
    private UserTenantEntity userTenantWithAccess;
    private UserTenantEntity userTenantWithoutAccess;
    private TipoDeOcorrenciaEntity tipoDeOcorrenciaEntity;

    @BeforeEach
    void setUp() {
        tenantExternalId = "tenant-ext-123";
        tenantId = 1L;
        tipoDeOcorrenciaId = 1L;

        Authorities authoritiesWithAccess = Authorities.builder()
                .cadastros(PermissionsCadastro.builder()
                        .tiposDeOcorrencia(true)
                        .build())
                .build();

        Authorities authoritiesWithoutAccess = Authorities.builder()
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

        tipoDeOcorrenciaEntity = TipoDeOcorrenciaEntity.builder()
                .id(tipoDeOcorrenciaId)
                .descricao("Tipo de Ocorrência para deletar")
                .ativo(true)
                .tenantId(tenantId)
                .build();
    }

    @Test
    void deveExecutarComSucessoQuandoUsuarioTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithAccess);
        when(getTipoDeOcorrenciaByIdAndTenantIdService.execute(tipoDeOcorrenciaId, tenantId)).thenReturn(tipoDeOcorrenciaEntity);
        when(tipoDeOcorrenciaRepository.save(any(TipoDeOcorrenciaEntity.class))).thenReturn(tipoDeOcorrenciaEntity);

        deleteTipoDeOcorrenciaService.execute(tipoDeOcorrenciaId, tenantExternalId, userTenants);

        verify(tipoDeOcorrenciaRepository, times(1)).save(argThat(tipoDeOcorrencia ->
                !tipoDeOcorrencia.getAtivo() &&
                tipoDeOcorrencia.getId().equals(tipoDeOcorrenciaId)
        ));
    }

    @Test
    void deveLancarForbiddenExceptionQuandoUsuarioNaoTemPermissao() {
        when(getTenantIdByIdExternoService.execute(tenantExternalId)).thenReturn(tenantId);
        when(getCurrentUserTenantService.execute(userTenants, tenantId)).thenReturn(userTenantWithoutAccess);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> deleteTipoDeOcorrenciaService.execute(tipoDeOcorrenciaId, tenantExternalId, userTenants)
        );

        assertNotNull(exception);
        verify(tipoDeOcorrenciaRepository, never()).save(any());
    }
}

