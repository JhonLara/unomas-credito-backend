package com.unomas.creditos.application.port;

import java.util.Map;

public interface CreditoPort {
    Map<String, Object> obtenerCreditoPorCedula(String cedula);
}
