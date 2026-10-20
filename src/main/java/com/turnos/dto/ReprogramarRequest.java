package com.turnos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReprogramarRequest(
        @NotNull(message = "La nueva fecha y hora son obligatorias") LocalDateTime fechaHoraInicio,
        @Min(value = 5, message = "La duración mínima es de 5 minutos") Integer duracionMinutos
) {
}
