/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidor_central.controllers;

import com.itson.servidor_central.models.CalificacionDTO;
import com.itson.servidor_central.websockets.AlertasHandler;
import java.util.List;
import java.util.Map;
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

    private final AlertasHandler alertasHandler;

    public ProgresoController(AlertasHandler alertasHandler) {
        this.alertasHandler = alertasHandler;
    }

    @GetMapping("/api/padres/progreso")
    public Object obtenerProgresoAcademico() {
        System.out.println("[Servidor Central] Recibiendo petición de la App Móvil/Web...");

        String urlMoodleMock = "http://moodle-mock:9090/api/calificaciones";
        RestTemplate restTemplate = new RestTemplate();

        try
        {
            CalificacionDTO respuestaMoodle = restTemplate.getForObject(urlMoodleMock, CalificacionDTO.class);

            if (respuestaMoodle != null && respuestaMoodle.materias != null)
            {
                for (CalificacionDTO.MateriaDTO materia : respuestaMoodle.materias)
                {
                    if (materia.calificacion < 6.0)
                    {
                        String mensajeAlerta = "¡Atención! Rendimiento bajo detectado en "
                                + materia.nombre
                                + " (Calificación: " + materia.calificacion + ")";

                        System.out.println("[Servidor Central] Disparando alerta por WebSocket...");
                        alertasHandler.enviarAlertaPush(mensajeAlerta);
                    }
                }
            }

            return respuestaMoodle;

        } catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
            return "{\"error\": \"No se pudo conectar con el sistema de calificaciones\"}";
        }
    }
}
