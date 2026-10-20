package com.turnos.config;

import com.turnos.dto.TurnoRequest;
import com.turnos.model.Paciente;
import com.turnos.model.Tratamiento;
import com.turnos.repository.PacienteRepository;
import com.turnos.repository.TratamientoRepository;
import com.turnos.service.TurnoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Carga datos de prueba (el paciente "Pepito" y algunos tratamientos) la primera vez que arranca la app.
 * Para desactivarlo: app.datos-ejemplo=false en application.properties.
 */
@Component
@ConditionalOnProperty(name = "app.datos-ejemplo", havingValue = "true")
public class DatosDeEjemplo implements CommandLineRunner {

    private final PacienteRepository pacienteRepository;
    private final TratamientoRepository tratamientoRepository;
    private final TurnoService turnoService;

    public DatosDeEjemplo(PacienteRepository pacienteRepository,
                          TratamientoRepository tratamientoRepository,
                          TurnoService turnoService) {
        this.pacienteRepository = pacienteRepository;
        this.tratamientoRepository = tratamientoRepository;
        this.turnoService = turnoService;
    }

    @Override
    public void run(String... args) {
        if (tratamientoRepository.count() == 0) {
            tratamientoRepository.save(new Tratamiento("Consulta", new BigDecimal("10000")));
            tratamientoRepository.save(new Tratamiento("Limpieza dental", new BigDecimal("15000")));
            tratamientoRepository.save(new Tratamiento("Arreglo de caries", new BigDecimal("25000")));
            tratamientoRepository.save(new Tratamiento("Extracción", new BigDecimal("30000")));
        }

        if (pacienteRepository.count() == 0) {
            Paciente pepito = pacienteRepository.save(
                    new Paciente("Pepito", "Pérez", "1234", "3794000000", "pepito@ejemplo.com", "Sin obra social"));

            Tratamiento limpieza = tratamientoRepository.findAll().stream()
                    .filter(t -> t.getNombre().equals("Limpieza dental"))
                    .findFirst()
                    .orElseThrow();

            turnoService.crear(new TurnoRequest(
                    pepito.getId(),
                    limpieza.getId(),
                    LocalDate.now().plusDays(1).atTime(10, 0),
                    30,
                    null,
                    "Turno de ejemplo"));
        }
    }
}
