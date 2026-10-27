package com.turnos.controller;

import com.turnos.dto.FichaDentalDto;
import com.turnos.service.FichaDentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes/{pacienteId}/ficha")
public class FichaDentalController {

    private final FichaDentalService fichaDentalService;

    public FichaDentalController(FichaDentalService fichaDentalService) {
        this.fichaDentalService = fichaDentalService;
    }

    @GetMapping
    public ResponseEntity<FichaDentalDto> obtener(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(fichaDentalService.obtenerPorPaciente(pacienteId));
    }

    @PutMapping
    public ResponseEntity<FichaDentalDto> guardar(@PathVariable Long pacienteId, @RequestBody FichaDentalDto dto) {
        return ResponseEntity.ok(fichaDentalService.guardarOActualizar(pacienteId, dto));
    }
}