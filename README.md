# Uno+1 Créditos Backend

API Spring Boot 3.3 + Java 17 para la plataforma de consulta de créditos UNO+1 Inversiones.

## Features

- Autenticación JWT (login/registro/recuperación de contraseña)
- Consulta de crédito por cédula (mock o API real Sadmin)
- Base de datos H2 (dev) / PostgreSQL (prod)
- Arquitectura hexagonal con puertos/adaptadores

## Requisitos

- Java 17
- Maven 3.9+

## Ejecutar localmente

```bash
mvn spring-boot:run
```

Perfiles disponibles:

| Profile | Base de datos | Créditos |
|---------|--------------|----------|
| default | H2 in-memory | Mock |
| `api` | H2 in-memory | API real Sadmin |
| `prod` | PostgreSQL | Mock |
| `prod,api` | PostgreSQL | API real Sadmin |

```bash
# Desarrollo con API real de créditos
mvn spring-boot:run -Dspring-boot.run.profiles=api
```

## Docker Compose (local)

```bash
docker-compose up -d
```

Levanta PostgreSQL + backend en `http://localhost:8080`.

## Variables de entorno

Ver `.env.example` para la lista completa. Las más importantes:

| Variable | Descripción |
|----------|-------------|
| `JWT_SECRET` | Clave para firmar tokens (mínimo 32 chars) |
| `DATABASE_URL` | URL JDBC PostgreSQL (Railway la provee automáticamente) |
| `SPRING_PROFILES_ACTIVE` | `prod`, `api`, o `prod,api` |
| `SADMIN_USERNAME` | Usuario API Sadmin |
| `SADMIN_PASSWORD` | Contraseña API Sadmin |
| `SADMIN_CREDITO_URL` | Endpoint de créditos (`https://api.sadmin.net/creditos/{cedula}`) |

## Despliegue en Railway

1. Crear proyecto en [railway.app](https://railway.app)
2. Conectar repositorio GitHub
3. Railway detecta automáticamente el `Dockerfile`
4. Agregar **PostgreSQL** desde el marketplace
5. Configurar variables de entorno en **Settings > Variables**
6. Deploy automático en cada push a `main`

## Endpoints principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registro de usuario |
| POST | `/api/auth/login` | Login (devuelve JWT) |
| POST | `/api/auth/password/forgot` | Solicitar OTP |
| POST | `/api/auth/password/reset` | Cambiar contraseña |
| GET | `/api/creditos/{cedula}` | Consultar crédito |
| GET | `/api/payment/redirect-url` | URL de pago Padlock |

## H2 Console (solo dev)

- URL: http://localhost:8080/h2-console
- JDBC: `jdbc:h2:mem:unomasdb`
- User: `sa`
- Password: vacío
