/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidor_central.controllers;

import com.itson.servidorcentral.models.CalificacionDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author rramirez
 */
@RestController
@CrossOrigin(origins = "*")
public class ProgresoController {

    @GetMapping("/api/padres/progreso")
    public Object obtenerProgresoAcademico() {
        System.out.println("[Servidor Central] Recibiendo petición de la App Móvil/Web...");

        // El Servidor Central actúa como cliente para consultar al Mock de Moodle
        String urlMoodleMock = "http://localhost:9090/api/calificaciones";
        RestTemplate restTemplate = new RestTemplate();

        try
        {
            System.out.println("[Servidor Central] Consultando al Sistema Externo Moodle...");
            // Petición GET síncrona al Mock 
            CalificacionDTO respuestaMoodle = restTemplate.getForObject(urlMoodleMock, CalificacionDTO.class);

            System.out.println("[Servidor Central] Datos obtenidos correctamente. Devolviendo a la App.");
            return respuestaMoodle;

        } catch (Exception e)
        {
            System.err.println("Error de comunicación con Moodle: " + e.getMessage());
            return "{\"error\": \"No se pudo conectar con el sistema de calificaciones\"}";
        }
    }
}
