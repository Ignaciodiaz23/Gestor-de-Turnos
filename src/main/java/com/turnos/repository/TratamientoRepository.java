package com.turnos.repository;

import com.turnos.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    boolean existsByNombre(String nombre);
}
