package com.turnos.dto;

public class FichaDentalDto {
    private String alergias;
    private String enfermedades;
    private String odontogramaJson;

    public FichaDentalDto() {}

    public FichaDentalDto(String alergias, String enfermedades, String odontogramaJson) {
        this.alergias = alergias;
        this.enfermedades = enfermedades;
        this.odontogramaJson = odontogramaJson;
    }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getEnfermedades() { return enfermedades; }
    public void setEnfermedades(String enfermedades) { this.enfermedades = enfermedades; }

    public String getOdontogramaJson() { return odontogramaJson; }
    public void setOdontogramaJson(String odontogramaJson) { this.odontogramaJson = odontogramaJson; }
}