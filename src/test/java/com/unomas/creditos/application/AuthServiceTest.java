package com.unomas.creditos.application;

import com.unomas.creditos.infrastructure.persistence.entity.PasswordResetEntity;
import com.unomas.creditos.infrastructure.persistence.entity.UserEntity;
import com.unomas.creditos.infrastructure.persistence.repository.PasswordResetJpaRepository;
import com.unomas.creditos.infrastructure.persistence.repository.UserJpaRepository;
import com.unomas.creditos.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserJpaRepository userRepository;
    @Mock PasswordResetJpaRepository passwordResetRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @Test
    void register_shouldSaveUser_whenCedulaNotExists() {
        when(userRepository.existsByCedula("123")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> result = authService.register("123", "a@b.com", "300", "password");

        assertEquals("Usuario registrado correctamente", result.get("message"));
        assertEquals("123", result.get("cedula"));
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void register_shouldThrow_whenCedulaExists() {
        when(userRepository.existsByCedula("123")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.register("123", "a@b.com", "300", "password"));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() {
        UserEntity user = UserEntity.builder().cedula("123").passwordHash("encoded").build();
        when(userRepository.findByCedula("123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtService.generateToken("123")).thenReturn("token123");

        Map<String, Object> result = authService.login("123", "password");

        assertEquals("token123", result.get("token"));
        assertEquals("123", result.get("cedula"));
    }

    @Test
    void login_shouldThrow_whenPasswordInvalid() {
        UserEntity user = UserEntity.builder().cedula("123").passwordHash("encoded").build();
        when(userRepository.findByCedula("123")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login("123", "wrong"));
    }

    @Test
    void forgotPassword_shouldGenerateOtp() {
        UserEntity user = UserEntity.builder().cedula("123").email("a@b.com").celular("300").build();
        when(userRepository.findByCedula("123")).thenReturn(Optional.of(user));
        when(passwordResetRepository.save(any(PasswordResetEntity.class))).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> result = authService.forgotPassword("123");

        assertNotNull(result.get("otp"));
        assertEquals("a@b.com", result.get("email"));
        verify(passwordResetRepository).save(any(PasswordResetEntity.class));
    }

    @Test
    void resetPassword_shouldUpdatePassword_whenOtpValid() {
        UserEntity user = UserEntity.builder().cedula("123").passwordHash("old").build();
        PasswordResetEntity reset = PasswordResetEntity.builder().user(user).codigoOtp("000000").usado(false).build();
        reset.setFechaExpiracion(java.time.LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByCedula("123")).thenReturn(Optional.of(user));
        when(passwordResetRepository.findTopByUserAndCodigoOtpAndUsadoFalseOrderByFechaExpiracionDesc(user, "000000"))
            .thenReturn(Optional.of(reset));
        when(passwordEncoder.encode("newpass")).thenReturn("newencoded");

        Map<String, Object> result = authService.resetPassword("123", "000000", "newpass");

        assertEquals("Contraseña actualizada correctamente", result.get("message"));
        assertEquals("newencoded", user.getPasswordHash());
        assertTrue(reset.isUsado());
    }
}
