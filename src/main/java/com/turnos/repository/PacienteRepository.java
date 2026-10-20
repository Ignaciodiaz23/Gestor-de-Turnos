package com.turnos.repository;

import com.turnos.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByDni(String dni);

    Optional<Paciente> findByDni(String dni);

    @Query("""
            select p from Paciente p
            where lower(p.apellido) like lower(concat('%', :q, '%'))
               or lower(p.nombre) like lower(concat('%', :q, '%'))
               or p.dni like concat('%', :q, '%')
            order by p.apellido, p.nombre
            """)
    List<Paciente> buscar(@Param("q") String q);
}
