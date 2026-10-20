package com.turnos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tratamiento es obligatorio")
    @Column(unique = true)
    private String nombre;

    @NotNull(message = "El importe base es obligatorio")
    @PositiveOrZero(message = "El importe no puede ser negativo")
    @Column(precision = 12, scale = 2)
    private BigDecimal importeBase;

    public Tratamiento() {
    }

    public Tratamiento(String nombre, BigDecimal importeBase) {
        this.nombre = nombre;
        this.importeBase = importeBase;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getImporteBase() { return importeBase; }
    public void setImporteBase(BigDecimal importeBase) { this.importeBase = importeBase; }
}
