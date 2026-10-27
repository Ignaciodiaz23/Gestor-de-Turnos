package com.turnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ficha_dental")
public class FichaDental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    @JsonIgnore
    private Paciente paciente;

    @Column(name = "alergias", length = 500)
    private String alergias;

    @Column(name = "enfermedades", length = 500)
    private String enfermedades;

    @Lob
    @Column(name = "odontograma_json", columnDefinition = "NVARCHAR(MAX)")
    private String odontogramaJson;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    @PreUpdate
    public void registrarFecha() {
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public FichaDental() {}

    public FichaDental(Paciente paciente, String alergias, String enfermedades, String odontogramaJson) {
        this.paciente = paciente;
        this.alergias = alergias;
        this.enfermedades = enfermedades;
        this.odontogramaJson = odontogramaJson;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getEnfermedades() { return enfermedades; }
    public void setEnfermedades(String enfermedades) { this.enfermedades = enfermedades; }

    public String getOdontogramaJson() { return odontogramaJson; }
    public void setOdontogramaJson(String odontogramaJson) { this.odontogramaJson = odontogramaJson; }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }
}