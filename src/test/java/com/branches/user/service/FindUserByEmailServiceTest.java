package com.branches.user.service;

import com.branches.user.domain.UserEntity;
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
class FindUserByEmailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FindUserByEmailService findUserByEmailService;

    private UserEntity userEntity;
    private String email;

    @BeforeEach
    void setUp() {
        email = "joao@example.com";

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

        UserTenantEntity userTenant = new UserTenantEntity();
        userTenant.setUser(userEntity);
        userTenant.setTenantId(1L);
        userTenant.setPerfil(ADMINISTRADOR);
        userTenant.setarId();
        userTenant.setUserObraPermitidaEntities(Set.of(new UserObraPermitidaEntity(UserObraPermitidaKey.from(userTenant, 1L), userTenant, 1L)));

        userEntity.setUserTenantEntities(List.of(userTenant));
    }

    @Test
    void deveRetornarOptionalComUserQuandoUsuarioEncontrado() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        Optional<UserEntity> resultado = findUserByEmailService.execute(email);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("user-ext-123", resultado.get().getIdExterno());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals(email, resultado.get().getEmail());
        assertEquals("senhaEncriptada123", resultado.get().getPassword());
        assertEquals("Engenheiro", resultado.get().getCargo());
        assertEquals(Role.USER, resultado.get().getRole());
        assertEquals("http://foto.url/joao.jpg", resultado.get().getFotoUrl());
        assertTrue(resultado.get().getAtivo());
        assertEquals(List.of(1L), resultado.get().getTenantsIds());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void deveRetornarOptionalVazioQuandoUsuarioNaoEncontrado() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<UserEntity> resultado = findUserByEmailService.execute(email);

        assertFalse(resultado.isPresent());

        verify(userRepository, times(1)).findByEmail(email);
    }
}

