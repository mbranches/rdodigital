package com.branches.user.service;

import com.branches.shared.dto.UserDto;
import com.branches.user.domain.UserEntity;
import com.branches.user.domain.UserTenantEntity;
import com.branches.user.domain.UserTenantKey;
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
class GetUserByIdExternoServiceTest {

    @Mock
    private LoadUserPort loadUserPort;

    @InjectMocks
    private GetUserByIdExternoService getUserByIdExternoService;

    private UserEntity userEntity;
    private String idExterno;

    @BeforeEach
    void setUp() {
        idExterno = "user-ext-123";

        userEntity = UserEntity.builder()
                .id(1L)
                .idExterno(idExterno)
                .nome("João Silva")
                .email("joao@example.com")
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

        userEntity.setUserTenantEntities(Set.of(userTenantEntity));
    }

    @Test
    void deveRetornarUserDtoQuandoUsuarioEncontrado() {
        when(loadUserPort.getByIdExterno(idExterno)).thenReturn(userEntity);

        UserDto resultado = getUserByIdExternoService.execute(idExterno);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("user-ext-123", resultado.idExterno());
        assertEquals("João Silva", resultado.nome());
        assertEquals("joao@example.com", resultado.email());
        assertEquals("senhaEncriptada123", resultado.password());
        assertEquals("Engenheiro", resultado.cargo());
        assertEquals(Role.USER, resultado.role());
        assertEquals("http://foto.url/joao.jpg", resultado.fotoUrl());
        assertTrue(resultado.ativo());
        assertEquals(List.of(1L), resultado.tenantIds());
    }
}