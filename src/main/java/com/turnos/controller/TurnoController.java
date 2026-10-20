package com.turnos.controller;

import com.turnos.dto.ReprogramarRequest;
import com.turnos.dto.TurnoRequest;
import com.turnos.model.Turno;
import com.turnos.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    /** Sin parámetro devuelve todos los turnos; con ?fecha=2026-10-20 devuelve solo los de ese día. */
    @GetMapping
    public List<Turno> listar(@RequestParam(required = false)
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return turnoService.listar(fecha);
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<Turno> listarPorPaciente(@PathVariable Long pacienteId) {
        return turnoService.listarPorPaciente(pacienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Turno crear(@Valid @RequestBody TurnoRequest request) {
        return turnoService.crear(request);
    }

    @PutMapping("/{id}/cancelar")
    public Turno cancelar(@PathVariable Long id) {
        return turnoService.cancelar(id);
    }

    @PutMapping("/{id}/reprogramar")
    public Turno reprogramar(@PathVariable Long id, @Valid @RequestBody ReprogramarRequest request) {
        return turnoService.reprogramar(id, request);
    }

    @PutMapping("/{id}/realizado")
    public Turno realizado(@PathVariable Long id) {
        return turnoService.marcarRealizado(id);
    }

    @PutMapping("/{id}/ausente")
    public Turno ausente(@PathVariable Long id) {
        return turnoService.marcarAusente(id);
    }

    @PutMapping("/{id}/cobrar")
    public Turno cobrar(@PathVariable Long id) {
        return turnoService.marcarCobrado(id);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        turnoService.eliminar(id);
    }
}
