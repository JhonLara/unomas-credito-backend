package com.unomas.creditos.infrastructure.rest;

import com.unomas.creditos.application.port.CreditoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/creditos")
@RequiredArgsConstructor
public class CreditoController {
    private final CreditoPort creditoPort;

    @GetMapping("/{cedula}")
    public ResponseEntity<Map<String, Object>> consultarCredito(@PathVariable String cedula) {
        return ResponseEntity.ok(creditoPort.obtenerCreditoPorCedula(cedula));
    }
}
