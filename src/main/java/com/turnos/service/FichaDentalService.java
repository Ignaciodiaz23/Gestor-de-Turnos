package com.turnos.service;

import com.turnos.dto.FichaDentalDto;
import com.turnos.model.FichaDental;
import com.turnos.model.Paciente;
import com.turnos.repository.FichaDentalRepository;
import com.turnos.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FichaDentalService {

    private final FichaDentalRepository fichaDentalRepository;
    private final PacienteRepository pacienteRepository;

    public FichaDentalService(FichaDentalRepository fichaDentalRepository, PacienteRepository pacienteRepository) {
        this.fichaDentalRepository = fichaDentalRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public FichaDentalDto obtenerPorPaciente(Long pacienteId) {
        return fichaDentalRepository.findByPacienteId(pacienteId)
                .map(f -> new FichaDentalDto(f.getAlergias(), f.getEnfermedades(), f.getOdontogramaJson()))
                .orElse(new FichaDentalDto("", "", "{}"));
    }

    @Transactional
    public FichaDentalDto guardarOActualizar(Long pacienteId, FichaDentalDto dto) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        FichaDental ficha = fichaDentalRepository.findByPacienteId(pacienteId)
                .orElseGet(() -> new FichaDental(paciente, "", "", "{}"));

        ficha.setAlergias(dto.getAlergias());
        ficha.setEnfermedades(dto.getEnfermedades());
        ficha.setOdontogramaJson(dto.getOdontogramaJson());

        fichaDentalRepository.save(ficha);
        return dto;
    }
}