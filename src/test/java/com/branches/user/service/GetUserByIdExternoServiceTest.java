package com.branches.user.service;

import com.branches.exception.NotFoundException;
import com.branches.user.domain.*;
import com.branches.user.domain.enums.Role;
import com.branches.user.repository.UserRepository;
import com.branches.usertenant.domain.UserObraPermitidaEntity;
import com.branches.usertenant.domain.UserObraPermitidaKey;
import com.branches.usertenant.domain.UserTenantEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.branches.usertenant.domain.enums.PerfilUserTenant.ADMINISTRADOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdExternoServiceTest {

    @Mock
    private UserRepository userRepository;

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

        UserTenantEntity userTenant = new UserTenantEntity();
        userTenant.setUser(userEntity);
        userTenant.setTenantId(1L);
        userTenant.setPerfil(ADMINISTRADOR);
        userTenant.setarId();
        userTenant.setUserObraPermitidaEntities(Set.of(new UserObraPermitidaEntity(UserObraPermitidaKey.from(userTenant, 1L), userTenant, 1L)));

        userEntity.setUserTenantEntities(List.of(userTenant));
    }

    @Test
    void deveRetornarUserDtoQuandoUsuarioEncontrado() {
        when(userRepository.findByIdExternoAndAtivoIsTrue(idExterno)).thenReturn(Optional.of(userEntity));

        UserEntity resultado = getUserByIdExternoService.execute(idExterno);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("user-ext-123", resultado.getIdExterno());
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@example.com", resultado.getEmail());
        assertEquals("senhaEncriptada123", resultado.getPassword());
        assertEquals("Engenheiro", resultado.getCargo());
        assertEquals(Role.USER, resultado.getRole());
        assertEquals("http://foto.url/joao.jpg", resultado.getFotoUrl());
        assertTrue(resultado.getAtivo());
        assertEquals(List.of(1L), resultado.getTenantsIds());
    }

    @Test
    void deveLancarNotFoundExceptionQuandoUsuarioNaoEncontrado() {
        when(userRepository.findByIdExternoAndAtivoIsTrue(idExterno)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> getUserByIdExternoService.execute(idExterno));

        String expectedMessage = "User não encontrado com idExterno: " + idExterno;

        assertEquals(expectedMessage, exception.getReason());
    }
}