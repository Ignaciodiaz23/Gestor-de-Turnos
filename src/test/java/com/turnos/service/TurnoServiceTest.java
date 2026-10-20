package com.turnos.service;

import com.turnos.dto.ReprogramarRequest;
import com.turnos.dto.TurnoRequest;
import com.turnos.exception.ReglaNegocioException;
import com.turnos.model.EstadoTurno;
import com.turnos.model.Paciente;
import com.turnos.model.Tratamiento;
import com.turnos.model.Turno;
import com.turnos.repository.PacienteRepository;
import com.turnos.repository.TratamientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba las reglas de los turnos contra una base H2 en memoria (no toca tu SQL Server).
 * Cada test se deshace al terminar (@Transactional).
 */
@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:turnos_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.datos-ejemplo=false"
})
class TurnoServiceTest {

    private static final LocalDateTime DIEZ = LocalDateTime.of(2030, 1, 14, 10, 0);

    @Autowired
    private TurnoService turnoService;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private TratamientoRepository tratamientoRepository;

    private Paciente paciente;
    private Tratamiento tratamiento;

    @BeforeEach
    void preparar() {
        paciente = pacienteRepository.save(new Paciente("Pepito", "Pérez", "1234", null, null, null));
        tratamiento = tratamientoRepository.save(new Tratamiento("Limpieza", new BigDecimal("15000")));
    }

    private TurnoRequest pedido(LocalDateTime inicio, int minutos) {
        return new TurnoRequest(paciente.getId(), tratamiento.getId(), inicio, minutos, null, null);
    }

    @Test
    void crearTurno_quedaProgramadoYUsaElImporteDelTratamiento() {
        Turno turno = turnoService.crear(pedido(DIEZ, 30));

        assertEquals(EstadoTurno.PROGRAMADO, turno.getEstado());
        assertEquals(DIEZ.plusMinutes(30), turno.getFechaHoraFin());
        assertEquals(0, new BigDecimal("15000").compareTo(turno.getImporte()));
        assertFalse(turno.isCobrado());
    }

    @Test
    void crearTurno_quePisaOtro_lanzaExcepcion() {
        turnoService.crear(pedido(DIEZ, 30));

        assertThrows(ReglaNegocioException.class,
                () -> turnoService.crear(pedido(DIEZ.plusMinutes(15), 30)));
    }

    @Test
    void crearTurno_justoCuandoTerminaOtro_esValido() {
        turnoService.crear(pedido(DIEZ, 30));

        Turno segundo = turnoService.crear(pedido(DIEZ.plusMinutes(30), 30));

        assertEquals(EstadoTurno.PROGRAMADO, segundo.getEstado());
    }

    @Test
    void cancelar_cambiaElEstadoYLiberaElHorario() {
        Turno turno = turnoService.crear(pedido(DIEZ, 30));

        Turno cancelado = turnoService.cancelar(turno.getId());
        Turno nuevo = turnoService.crear(pedido(DIEZ, 30));

        assertEquals(EstadoTurno.CANCELADO, cancelado.getEstado());
        assertEquals(EstadoTurno.PROGRAMADO, nuevo.getEstado());
    }

    @Test
    void cancelar_dosVeces_lanzaExcepcion() {
        Turno turno = turnoService.crear(pedido(DIEZ, 30));
        turnoService.cancelar(turno.getId());

        assertThrows(ReglaNegocioException.class, () -> turnoService.cancelar(turno.getId()));
    }

    @Test
    void reprogramar_mueveElTurnoYConservaLaDuracion() {
        Turno turno = turnoService.crear(pedido(DIEZ, 45));
        LocalDateTime nuevoInicio = DIEZ.plusHours(4);

        Turno movido = turnoService.reprogramar(turno.getId(), new ReprogramarRequest(nuevoInicio, null));

        assertEquals(nuevoInicio, movido.getFechaHoraInicio());
        assertEquals(nuevoInicio.plusMinutes(45), movido.getFechaHoraFin());
    }

    @Test
    void reprogramar_aUnHorarioOcupado_lanzaExcepcion() {
        turnoService.crear(pedido(DIEZ, 30));
        Turno otro = turnoService.crear(pedido(DIEZ.plusHours(1), 30));

        assertThrows(ReglaNegocioException.class,
                () -> turnoService.reprogramar(otro.getId(), new ReprogramarRequest(DIEZ.plusMinutes(15), null)));
    }

    @Test
    void reprogramar_haciaUnHorarioQueSoloSePisaConsigoMismo_esValido() {
        Turno turno = turnoService.crear(pedido(DIEZ, 30));

        Turno movido = turnoService.reprogramar(turno.getId(), new ReprogramarRequest(DIEZ.plusMinutes(15), null));

        assertEquals(DIEZ.plusMinutes(15), movido.getFechaHoraInicio());
    }

    @Test
    void cobrar_unTurnoCancelado_lanzaExcepcion() {
        Turno turno = turnoService.crear(pedido(DIEZ, 30));
        turnoService.cancelar(turno.getId());

        assertThrows(ReglaNegocioException.class, () -> turnoService.marcarCobrado(turno.getId()));
    }
}
