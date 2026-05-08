# Spec funcional base - Plataforma Uno+1 Créditos

## Objetivo
Permitir que el cliente final consulte la información de su crédito y sea redirigido a la plataforma externa de pago.

## Alcance MVP
- Registro de usuario desde pantalla principal.
- Login con cédula y contraseña.
- Recuperación de contraseña con correo o celular.
- Dashboard del crédito con datos mockeados temporalmente.
- Botón de pago con redirección a Padlock.

## Datos que guarda la plataforma
La plataforma solo guarda datos necesarios para autenticación y recuperación:
- Cédula
- Correo
- Celular
- Contraseña encriptada
- Estado del usuario

## Datos que NO guarda la plataforma
No se almacenan datos financieros del crédito:
- Saldos
- Cuotas
- Mora
- Pagos
- Historial financiero

Estos datos vendrán desde el API externo cuando esté disponible.

## Pago
URL de redirección:
https://micrositio.padlock.com.co/unotuno

## Mock temporal
Endpoint temporal:
GET /api/creditos/{cedula}

Permite simular:
- Cliente al día
- Cliente en mora
- Cuotas pagadas
- Cuotas pendientes
- Valor a pagar
