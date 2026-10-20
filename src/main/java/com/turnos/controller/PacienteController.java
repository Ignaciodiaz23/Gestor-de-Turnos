package com.turnos.controller;

import com.turnos.model.Paciente;
import com.turnos.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public List<Paciente> listar(@RequestParam(required = false) String buscar) {
        return pacienteService.listar(buscar);
    }

    @GetMapping("/{id}")
    public Paciente obtener(@PathVariable Long id) {
        return pacienteService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente crear(@Valid @RequestBody Paciente paciente) {
        return pacienteService.crear(paciente);
    }

    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id, @Valid @RequestBody Paciente paciente) {
        return pacienteService.actualizar(id, paciente);
    }
@DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
    }
}
