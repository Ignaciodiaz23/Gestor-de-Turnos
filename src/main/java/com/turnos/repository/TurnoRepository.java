package com.turnos.repository;

import com.turnos.model.EstadoTurno;
import com.turnos.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findAllByOrderByFechaHoraInicioAsc();

    List<Turno> findByFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(LocalDateTime desde, LocalDateTime hasta);

    List<Turno> findByPacienteIdOrderByFechaHoraInicioDesc(Long pacienteId);

    /**
     * Devuelve true si existe otro turno en el estado indicado que se pisa con el rango [inicio, fin).
     * Dos turnos se pisan cuando uno empieza antes de que el otro termine y termina después de que el otro empieza.
     */
    @Query("""
            select count(t) > 0 from Turno t
            where t.estado = :estado
              and t.id <> :excluirId
              and t.fechaHoraInicio < :fin
              and t.fechaHoraFin > :inicio
            """)
    boolean haySuperposicion(@Param("estado") EstadoTurno estado,
                             @Param("excluirId") Long excluirId,
                             @Param("inicio") LocalDateTime inicio,
                             @Param("fin") LocalDateTime fin);
}
