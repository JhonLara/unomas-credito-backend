package com.unomas.creditos.infrastructure.mock;

import com.unomas.creditos.application.port.CreditoPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile({"!prod", "!api"})
public class CreditoMockService implements CreditoPort {
    public Map<String, Object> obtenerCreditoPorCedula(String cedula) {
        boolean enMora = cedula.endsWith("1") || cedula.equals("11111111");
        Map<String, Object> credito = new HashMap<>();
        credito.put("numeroCredito", "CR-000123");
        credito.put("tipoCredito", "LIBRE INVERSIÓN");
        credito.put("saldoCapital", new BigDecimal("2500000"));
        credito.put("valorTotalCredito", new BigDecimal("5000000"));
        credito.put("cuotasPagadas", 8);
        credito.put("cuotasPendientes", 16);
        credito.put("valorCuota", new BigDecimal("320000"));
        credito.put("fechaProximoPago", LocalDate.now().plusDays(10).toString());
        credito.put("estado", enMora ? "EN_MORA" : "AL_DIA");
        credito.put("diasAtraso", enMora ? 12 : 0);
        credito.put("valorMora", enMora ? new BigDecimal("45000") : BigDecimal.ZERO);
        credito.put("valorPagar", enMora ? new BigDecimal("365000") : new BigDecimal("320000"));

        return Map.of(
                "identificacion", cedula,
                "nombre", "CRÉDITO PRUEBA",
                "correo", "analistamultintegral@gmail.com",
                "telefono", "3001234567",
                "direccion", "NANAN - PEREIRA",
                "credito", credito,
                "cuotas", List.of(
                        Map.of("numero", 1, "fecha", "2026-01-30", "valor", 320000, "estado", "PAGADA"),
                        Map.of("numero", 2, "fecha", "2026-02-28", "valor", 320000, "estado", "PAGADA"),
                        Map.of("numero", 3, "fecha", "2026-03-30", "valor", 320000, "estado", enMora ? "VENCIDA" : "PENDIENTE"),
                        Map.of("numero", 4, "fecha", "2026-04-30", "valor", 320000, "estado", "PENDIENTE")
                )
        );
    }
}
