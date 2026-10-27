package com.turnos.repository;

import com.turnos.model.FichaDental;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FichaDentalRepository extends JpaRepository<FichaDental, Long> {
    Optional<FichaDental> findByPacienteId(Long pacienteId);
    void deleteByPacienteId(Long pacienteId);
}