package com.example.pacientesjavafxequipo12.models;

/**
 * Clase Modelo que representa al Paciente.
 * Cumple con el requisito de POO (Encapsulamiento) como rubrica de evaluacion
 * puesta por el profe.
 */
public class Paciente {

    private String curp;
    private String nombre;
    private int edad;
    private String telefono;
    private String alergias;
    private String estatus;

    public Paciente(String curp, String nombre, int edad, String telefono, String alergias, String estatus) {
        this.curp = curp;
        this.nombre = nombre;
        this.edad = edad;
        this.telefono = telefono;
        this.alergias = alergias;
        this.estatus = estatus;
    }

    public Paciente() {
    }


    //getters (consultar) y setters (cambiar o modificar) para la tablaview
    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        return curp + "," + nombre + "," + edad + "," + telefono + "," + alergias + "," + estatus;
    }
}