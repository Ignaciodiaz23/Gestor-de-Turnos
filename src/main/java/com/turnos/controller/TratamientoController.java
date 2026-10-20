package com.turnos.controller;

import com.turnos.exception.ReglaNegocioException;
import com.turnos.model.Tratamiento;
import com.turnos.repository.TratamientoRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    private final TratamientoRepository tratamientoRepository;

    public TratamientoController(TratamientoRepository tratamientoRepository) {
        this.tratamientoRepository = tratamientoRepository;
    }

    @GetMapping
    public List<Tratamiento> listar() {
        return tratamientoRepository.findAll(Sort.by("nombre"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tratamiento crear(@Valid @RequestBody Tratamiento tratamiento) {
        if (tratamientoRepository.existsByNombre(tratamiento.getNombre())) {
            throw new ReglaNegocioException("Ya existe un tratamiento con ese nombre");
        }
        tratamiento.setId(null);
        return tratamientoRepository.save(tratamiento);
    }
}
