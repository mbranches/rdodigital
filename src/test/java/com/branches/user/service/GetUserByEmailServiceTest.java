package com.branches.user.service;

import com.branches.shared.dto.UserDto;
import com.branches.user.domain.*;
import com.branches.user.domain.enums.Role;
import com.branches.user.port.LoadUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByEmailServiceTest {

    @Mock
    private LoadUserPort loadUserPort;

    @InjectMocks
    private GetUserByEmailService getUserByEmailService;

    private UserEntity userEntity;
    private String email;

    @BeforeEach
    void setUp() {
        email = "teste@example.com";

        userEntity = UserEntity.builder()
                .id(1L)
                .idExterno("user-ext-123")
                .nome("João Silva")
                .email(email)
                .password("senhaEncriptada123")
                .cargo("Engenheiro")
                .role(Role.USER)
                .fotoUrl("http://foto.url/joao.jpg")
                .ativo(true)
                .build();

        UserTenantKey userTenantKey = UserTenantKey.from(userEntity.getId(), 1L);
        UserTenantEntity userTenantEntity = new UserTenantEntity();
        userTenantEntity.setId(userTenantKey);
        userTenantEntity.setUser(userEntity);
        userTenantEntity.setTenantId(1L);
        userTenantEntity.setUserObraPermitidaEntities(Set.of(new UserObraPermitidaEntity(UserObraPermitidaKey.from(userTenantEntity, 1L), userTenantEntity, 1L)));

        userEntity.setUserTenantEntities(Set.of(userTenantEntity));
    }

    @Test
    void deveRetornarUserDtoQuandoUsuarioEncontrado() {
        when(loadUserPort.loadByEmail(email)).thenReturn(userEntity);

        UserDto resultado = getUserByEmailService.execute(email);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("user-ext-123", resultado.idExterno());
        assertEquals("João Silva", resultado.nome());
        assertEquals(email, resultado.email());
        assertEquals("senhaEncriptada123", resultado.password());
        assertEquals("Engenheiro", resultado.cargo());
        assertEquals(Role.USER, resultado.role());
        assertEquals("http://foto.url/joao.jpg", resultado.fotoUrl());
        assertTrue(resultado.ativo());
        assertEquals(List.of(1L), resultado.tenantsIds());

        verify(loadUserPort, times(1)).loadByEmail(email);
    }
}