package com.turnos.service;

import com.turnos.dto.ReprogramarRequest;
import com.turnos.dto.TurnoRequest;
import com.turnos.exception.RecursoNoEncontradoException;
import com.turnos.exception.ReglaNegocioException;
import com.turnos.model.EstadoTurno;
import com.turnos.model.Paciente;
import com.turnos.model.Tratamiento;
import com.turnos.model.Turno;
import com.turnos.repository.PacienteRepository;
import com.turnos.repository.TratamientoRepository;
import com.turnos.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    /** Id que nunca existe, para usar cuando no hay que excluir ningún turno de la búsqueda de superposiciones. */
    private static final Long SIN_EXCLUIR = -1L;

    private final TurnoRepository turnoRepository;
    private final PacienteRepository pacienteRepository;
    private final TratamientoRepository tratamientoRepository;

    public TurnoService(TurnoRepository turnoRepository,
                        PacienteRepository pacienteRepository,
                        TratamientoRepository tratamientoRepository) {
        this.turnoRepository = turnoRepository;
        this.pacienteRepository = pacienteRepository;
        this.tratamientoRepository = tratamientoRepository;
    }

    @Transactional(readOnly = true)
    public List<Turno> listar(LocalDate fecha) {
        if (fecha == null) {
            return turnoRepository.findAllByOrderByFechaHoraInicioAsc();
        }
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.plusDays(1).atStartOfDay();
        return turnoRepository.findByFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(desde, hasta);
    }

    @Transactional(readOnly = true)
    public List<Turno> listarPorPaciente(Long pacienteId) {
        return turnoRepository.findByPacienteIdOrderByFechaHoraInicioDesc(pacienteId);
    }

    @Transactional
    public Turno crear(TurnoRequest req) {
        Paciente paciente = pacienteRepository.findById(req.pacienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado"));
        Tratamiento tratamiento = tratamientoRepository.findById(req.tratamientoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tratamiento no encontrado"));

        LocalDateTime inicio = req.fechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(req.duracionMinutos());
        validarSinSuperposicion(inicio, fin, SIN_EXCLUIR);

        Turno turno = new Turno();
        turno.setPaciente(paciente);
        turno.setTratamiento(tratamiento);
        turno.setFechaHoraInicio(inicio);
        turno.setFechaHoraFin(fin);
        turno.setImporte(req.importe() != null ? req.importe() : tratamiento.getImporteBase());
        turno.setObservaciones(req.observaciones());
        turno.setEstado(EstadoTurno.PROGRAMADO);
        turno.setCobrado(false);
        return turnoRepository.save(turno);
    }

    /** Cancelar no borra el turno: cambia el estado, así queda el historial y el horario se libera. */
    @Transactional
    public Turno cancelar(Long id) {
        Turno turno = obtener(id);
        exigirProgramado(turno, "cancelar");
        turno.setEstado(EstadoTurno.CANCELADO);
        return turnoRepository.save(turno);
    }

    @Transactional
    public Turno reprogramar(Long id, ReprogramarRequest req) {
        Turno turno = obtener(id);
        exigirProgramado(turno, "reprogramar");

        long duracionActual = Duration.between(turno.getFechaHoraInicio(), turno.getFechaHoraFin()).toMinutes();
        long duracion = req.duracionMinutos() != null ? req.duracionMinutos() : duracionActual;

        LocalDateTime inicio = req.fechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(duracion);
        validarSinSuperposicion(inicio, fin, turno.getId());

        turno.setFechaHoraInicio(inicio);
        turno.setFechaHoraFin(fin);
        return turnoRepository.save(turno);
    }

    @Transactional
    public Turno marcarRealizado(Long id) {
        Turno turno = obtener(id);
        exigirProgramado(turno, "marcar como realizado");
        turno.setEstado(EstadoTurno.REALIZADO);
        return turnoRepository.save(turno);
    }

    @Transactional
    public Turno marcarAusente(Long id) {
        Turno turno = obtener(id);
        exigirProgramado(turno, "marcar como ausente");
        turno.setEstado(EstadoTurno.AUSENTE);
        return turnoRepository.save(turno);
    }

    @Transactional
    public Turno marcarCobrado(Long id) {
        Turno turno = obtener(id);
        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new ReglaNegocioException("No se puede cobrar un turno cancelado");
        }
        turno.setCobrado(true);
        return turnoRepository.save(turno);
    }

    @Transactional(readOnly = true)
    public Turno obtener(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Turno no encontrado"));
    }

    private void exigirProgramado(Turno turno, String accion) {
        if (turno.getEstado() != EstadoTurno.PROGRAMADO) {
            throw new ReglaNegocioException(
                    "No se puede " + accion + " un turno que está " + turno.getEstado().name().toLowerCase());
        }
    }

    private void validarSinSuperposicion(LocalDateTime inicio, LocalDateTime fin, Long excluirId) {
        if (turnoRepository.haySuperposicion(EstadoTurno.PROGRAMADO, excluirId, inicio, fin)) {
            throw new ReglaNegocioException("Ese horario se superpone con otro turno programado");
        }
    }
    public void eliminar(Long id) {
    turnoRepository.deleteById(id);
}
}
