package com.unomas.creditos.application;

import com.unomas.creditos.infrastructure.persistence.entity.PasswordResetEntity;
import com.unomas.creditos.infrastructure.persistence.entity.UserEntity;
import com.unomas.creditos.infrastructure.persistence.repository.PasswordResetJpaRepository;
import com.unomas.creditos.infrastructure.persistence.repository.UserJpaRepository;
import com.unomas.creditos.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserJpaRepository userRepository;
    private final PasswordResetJpaRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public Map<String, Object> register(String cedula, String email, String celular, String password) {
        if (userRepository.existsByCedula(cedula)) throw new IllegalArgumentException("La cédula ya está registrada");
        UserEntity user = UserEntity.builder()
                .cedula(cedula)
                .email(email)
                .celular(celular)
                .passwordHash(passwordEncoder.encode(password))
                .estado("ACTIVO")
                .build();
        userRepository.save(user);
        return Map.of("message", "Usuario registrado correctamente", "cedula", user.getCedula());
    }

    public Map<String, Object> login(String cedula, String password) {
        UserEntity user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña inválidos"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Usuario o contraseña inválidos");
        }
        return Map.of("token", jwtService.generateToken(user.getCedula()), "cedula", user.getCedula());
    }

    @Transactional
    public Map<String, Object> forgotPassword(String cedula) {
        UserEntity user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new IllegalArgumentException("No existe usuario con esa cédula"));
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        passwordResetRepository.save(PasswordResetEntity.builder()
                .user(user).codigoOtp(otp).fechaExpiracion(LocalDateTime.now().plusMinutes(15)).usado(false).build());
        // TODO: integrar envío real por email/SMS. De momento se retorna para pruebas.
        return Map.of("message", "Código OTP generado para pruebas", "otp", otp, "email", user.getEmail(), "celular", user.getCelular());
    }

    @Transactional
    public Map<String, Object> resetPassword(String cedula, String otp, String newPassword) {
        UserEntity user = userRepository.findByCedula(cedula)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        PasswordResetEntity reset = passwordResetRepository.findTopByUserAndCodigoOtpAndUsadoFalseOrderByFechaExpiracionDesc(user, otp)
                .orElseThrow(() -> new IllegalArgumentException("OTP inválido"));
        if (reset.getFechaExpiracion().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("OTP expirado");
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        reset.setUsado(true);
        return Map.of("message", "Contraseña actualizada correctamente");
    }
}
