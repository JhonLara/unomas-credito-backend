package com.unomas.creditos.infrastructure.persistence.repository;

import com.unomas.creditos.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByCedula(String cedula);
    boolean existsByCedula(String cedula);
}
