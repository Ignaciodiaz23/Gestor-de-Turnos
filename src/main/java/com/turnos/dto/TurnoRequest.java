package com.turnos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TurnoRequest(
        @NotNull(message = "Elegí un paciente") Long pacienteId,
        @NotNull(message = "Elegí un tratamiento") Long tratamientoId,
        @NotNull(message = "La fecha y hora son obligatorias") LocalDateTime fechaHoraInicio,
        @NotNull(message = "La duración es obligatoria") @Min(value = 5, message = "La duración mínima es de 5 minutos") Integer duracionMinutos,
        @PositiveOrZero(message = "El importe no puede ser negativo") BigDecimal importe,
        String observaciones
) {
}
