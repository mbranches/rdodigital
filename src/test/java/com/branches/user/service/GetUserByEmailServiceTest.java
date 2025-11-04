package com.branches.user.service;

import com.branches.shared.dto.UserDto;
import com.branches.user.domain.UserEntity;
import com.branches.user.domain.enums.Role;
import com.branches.user.port.LoadUserPort;
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
                .tenantIds(List.of(1L, 2L, 3L))
                .build();
    }

    @Test
    void deveRetornarUserDtoQuandoUsuarioEncontrado() {
        when(loadUserPort.loadByEmail(email)).thenReturn(userEntity);

        UserDto resultado = getUserByEmailService.execute(email);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("user-ext-123", resultado.idExterno());
        assertEquals("João Silva", resultado.nome());
        assertEquals("senhaEncriptada123", resultado.email());
        assertEquals(email, resultado.password());
        assertEquals("Engenheiro", resultado.cargo());
        assertEquals(Role.USER, resultado.role());
        assertEquals("http://foto.url/joao.jpg", resultado.fotoUrl());
        assertTrue(resultado.ativo());
        assertEquals(List.of(1L, 2L, 3L), resultado.tenantIds());

        verify(loadUserPort, times(1)).loadByEmail(email);
    }
}