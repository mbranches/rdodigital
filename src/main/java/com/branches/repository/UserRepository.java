package com.branches.repository;

import com.branches.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByIdExterno(String idExterno);

    Optional<UserEntity> findByEmail(String email);
}
