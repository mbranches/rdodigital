package com.branches.user.repository;

import com.branches.user.domain.UserEntity;
import com.branches.user.repository.projection.UserInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByIdExternoAndAtivoIsTrue(String idExterno);

    Optional<UserEntity> findByEmail(String email);

    @Query("""
    SELECT u.idExterno AS idExterno,
           u.nome AS nome,
           u.email AS email,
           ut.cargo AS cargo,
           u.fotoUrl AS fotoUrl,
           ut.authorities as authorities,
           ut.perfil AS perfil,
           a.assinaturaUrl AS assinaturaUrl
    FROM UserEntity u
    JOIN UserTenantEntity ut ON u.id = ut.user.id AND ut.tenantId = :tenantId
    LEFT JOIN u.assinatura a
    WHERE u.id = :userId
        AND u.ativo = true
        AND ut.ativo = true
""")
    Optional<UserInfoProjection> findUserInfoByIdAndTenantId(Long userId, Long tenantId);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByIdAndAtivoIsTrue(Long id);
}
