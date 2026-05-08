package com.unomas.creditos.infrastructure.persistence.repository;

import com.unomas.creditos.infrastructure.persistence.entity.PasswordResetEntity;
import com.unomas.creditos.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetJpaRepository extends JpaRepository<PasswordResetEntity, UUID> {
    Optional<PasswordResetEntity> findTopByUserAndCodigoOtpAndUsadoFalseOrderByFechaExpiracionDesc(UserEntity user, String codigoOtp);
}
