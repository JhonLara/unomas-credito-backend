package com.unomas.creditos.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SadminTokenClient {

    @Value("${app.sadmin.token-url:https://api.sadmin.net/token}")
    private String tokenUrl;

    @Value("${app.sadmin.username:}")
    private String username;

    @Value("${app.sadmin.password:}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    private String cachedToken;
    private Instant tokenExpiry;

    public synchronized String obtenerToken() {
        if (cachedToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry.minusSeconds(60))) {
            return cachedToken;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<SadminTokenResponse> response = restTemplate.postForEntity(
                tokenUrl, request, SadminTokenResponse.class
            );

            if (response.getBody() == null || response.getBody().accessToken() == null) {
                throw new RuntimeException("Respuesta de token inválida desde Sadmin");
            }

            SadminTokenResponse token = response.getBody();
            cachedToken = token.accessToken();
            long expiresIn = token.expiresIn() != null ? token.expiresIn() : 3600;
            tokenExpiry = Instant.now().plusSeconds(expiresIn);

            log.info("Token Sadmin obtenido correctamente, expira en {} segundos", expiresIn);
            return cachedToken;
        } catch (Exception e) {
            log.error("Error obteniendo token de Sadmin: {}", e.getMessage(), e);
            throw new RuntimeException("No fue posible autenticar con el API externo de créditos", e);
        }
    }
}
