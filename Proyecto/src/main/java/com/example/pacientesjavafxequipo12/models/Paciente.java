package com.example.pacientesjavafxequipo12.models;

public class Paciente {
    private String curp;
    private String nombre;
    private int edad;
    private String telefono;
    private String alergias;
    private String estatus; // ACTIVO / INACTIVO

    public Paciente(String curp, String nombre, int edad, String telefono, String alergias, String estatus) {
        this.curp = curp;
        this.nombre = nombre;
        this.edad = edad;
        this.telefono = telefono;
        this.alergias = alergias;
        this.estatus = estatus;
    }

    // Getters necesarios para el TableView
    public String getCurp() { return curp; }
    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public String getTelefono() { return telefono; }
    public String getAlergias() { return alergias; }
    public String getEstatus() { return estatus; }

    // Setters para actualización y borrado lógico
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setAlergias(String alergias) { this.alergias = alergias; }
    public void setEstatus(String estatus) { this.estatus = estatus; }

    @Override
    public String toString() {
        return curp + "," + nombre + "," + edad + "," + telefono + "," + alergias + "," + estatus;
    }
}