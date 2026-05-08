package com.unomas.creditos.infrastructure.client;

import com.unomas.creditos.application.port.CreditoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@Primary
@Profile("api")
@RequiredArgsConstructor
public class CreditoApiClient implements CreditoPort {

    private final SadminTokenClient tokenClient;

    @Value("${app.sadmin.credito-url:}")
    private String creditoUrlTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Map<String, Object> obtenerCreditoPorCedula(String cedula) {
        if (creditoUrlTemplate == null || creditoUrlTemplate.isBlank()) {
            throw new IllegalStateException("La URL del API de créditos no está configurada (app.sadmin.credito-url)");
        }

        String token = tokenClient.obtenerToken();
        String url = creditoUrlTemplate.replace("{cedula}", cedula);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
            );
            log.info("Crédito obtenido desde API externa para cédula {}", cedula);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error consultando crédito en API externa para cédula {}: {}", cedula, e.getMessage(), e);
            throw new RuntimeException("No fue posible consultar la información del crédito", e);
        }
    }
}
