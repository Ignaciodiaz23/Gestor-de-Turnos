package com.turnos.service;

import com.turnos.exception.RecursoNoEncontradoException;
import com.turnos.exception.ReglaNegocioException;
import com.turnos.model.Paciente;
import com.turnos.repository.PacienteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Paciente> listar(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return pacienteRepository.findAll(Sort.by("apellido", "nombre"));
        }
        return pacienteRepository.buscar(busqueda.trim());
    }

    @Transactional(readOnly = true)
    public Paciente obtener(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado"));
    }

    @Transactional
    public Paciente crear(Paciente paciente) {
        if (pacienteRepository.existsByDni(paciente.getDni())) {
            throw new ReglaNegocioException("Ya existe un paciente con ese DNI");
        }
        paciente.setId(null);
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente actualizar(Long id, Paciente datos) {
        Paciente actual = obtener(id);

        boolean dniDeOtroPaciente = pacienteRepository.findByDni(datos.getDni())
                .filter(otro -> !otro.getId().equals(id))
                .isPresent();
        if (dniDeOtroPaciente) {
            throw new ReglaNegocioException("Ya existe otro paciente con ese DNI");
        }

        actual.setNombre(datos.getNombre());
        actual.setApellido(datos.getApellido());
        actual.setDni(datos.getDni());
        actual.setTelefono(datos.getTelefono());
        actual.setEmail(datos.getEmail());
        actual.setObraSocial(datos.getObraSocial());
return pacienteRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        pacienteRepository.deleteById(id);
    }
}