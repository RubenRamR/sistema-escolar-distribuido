/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidor_central.models;

import java.util.List;

public class CalificacionDTO {
    public String estudianteId;
    public String nombre;
    public String periodo;
    public List<MateriaDTO> materias;

    public static class MateriaDTO {
        public String id;
        public String nombre;
        public double calificacion;
    }
}
