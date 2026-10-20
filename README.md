# Turnos - Consultorio odontológico (versión de prueba)

Aplicación de práctica para registrar turnos de una odontóloga. Tiene las cuatro capas:

| Capa | Tecnología | Dónde está |
|---|---|---|
| Frontend | HTML + JavaScript | `src/main/resources/static/index.html` |
| Backend | Spring Boot (controladores y servicios) | `controller/`, `service/` |
| ORM | JPA / Hibernate | `model/` (entidades), `repository/` |
| Base de datos | SQL Server 2022 en Docker (OrbStack) | base `TurnosDB` |

## Qué hace

- Alta y búsqueda de pacientes (por apellido, nombre o DNI).
- Agendar turnos con paciente, tratamiento, fecha y hora, duración, importe y observaciones.
- No deja agendar dos turnos que se pisen en el horario.
- Cancelar (no borra: cambia el estado a `cancelado` y libera el horario).
- Reprogramar (cambiar fecha y hora, con la misma validación).
- Marcar como realizado, ausente o cobrado.
- Agenda filtrable por día.

Al arrancar por primera vez carga datos de ejemplo: el paciente **Pepito Pérez (DNI 1234)**, cuatro tratamientos y un turno para mañana a las 10:00.

## Requisitos

- Java 17 o superior (JDK).
- Maven (`brew install maven`), o las extensiones de Java y Spring Boot de VS Code.
- El contenedor `sqlserver` de OrbStack corriendo (`docker ps` tiene que mostrarlo "Up").

## Cómo correrlo

**1. Crear la base de datos.** En VS Code, con la conexión "Docker local", abrí una consulta nueva y ejecutá:

```sql
CREATE DATABASE TurnosDB;
```

**2. Revisar la contraseña** en `src/main/resources/application.properties`. Tiene que ser la misma que usaste en el `docker run`:

```properties
spring.datasource.password=TuClave_123
```

**3. Arrancar la aplicación.** Desde una terminal, dentro de la carpeta del proyecto:

```bash
mvn spring-boot:run
```

La primera vez descarga las dependencias y tarda unos minutos. Cuando aparece `Started TurnosApplication`, está lista.

**4. Abrir la página:** http://localhost:8080

Hibernate crea las tablas solo (`paciente`, `tratamiento`, `turno`). Podés verlas en VS Code: actualizá la conexión y mirá `TurnosDB > Tables`.

## Tests

Las reglas de los turnos (superposición, cancelar, reprogramar) se prueban con una base H2 en memoria, sin tocar tu SQL Server:

```bash
mvn test
```

## API (para probar con el navegador, Postman o curl)

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/pacientes?buscar=perez` | Lista o busca pacientes |
| POST | `/api/pacientes` | Crea un paciente |
| PUT | `/api/pacientes/{id}` | Modifica un paciente |
| GET | `/api/tratamientos` | Lista tratamientos |
| POST | `/api/tratamientos` | Crea un tratamiento |
| GET | `/api/turnos?fecha=2030-01-14` | Lista turnos (todos, o de un día) |
| GET | `/api/turnos/paciente/{id}` | Turnos de un paciente |
| POST | `/api/turnos` | Agenda un turno |
| PUT | `/api/turnos/{id}/cancelar` | Cancela |
| PUT | `/api/turnos/{id}/reprogramar` | Cambia fecha y hora |
| PUT | `/api/turnos/{id}/realizado` | Marca como realizado |
| PUT | `/api/turnos/{id}/ausente` | Marca como ausente |
| PUT | `/api/turnos/{id}/cobrar` | Marca como cobrado |

Ejemplo de cuerpo para `POST /api/turnos`:

```json
{
  "pacienteId": 1,
  "tratamientoId": 2,
  "fechaHoraInicio": "2030-01-14T10:00:00",
  "duracionMinutos": 30,
  "importe": null,
  "observaciones": "Primera consulta"
}
```

## Para convertirla en una app de uso real

Esta versión es para practicar. Antes de usarla con pacientes reales falta, como mínimo:

- Usuarios y contraseñas (login) para entrar a la aplicación.
- Un usuario de base de datos propio, en lugar de `sa`, y la contraseña fuera del código (variable de entorno).
- Copias de seguridad automáticas de la base.
- Conexión cifrada (HTTPS) si se accede desde otra computadora.
- Historia clínica y datos de salud con el cuidado que pide la ley (Ley 25.326 de protección de datos personales y Ley 26.529 de derechos del paciente).
- Validar que no se agenden turnos en el pasado y definir el horario de atención.
- Recordatorios a los pacientes (WhatsApp o email).
- Reemplazar `ddl-auto=update` por migraciones (Flyway) para poder cambiar la base sin perder datos.
