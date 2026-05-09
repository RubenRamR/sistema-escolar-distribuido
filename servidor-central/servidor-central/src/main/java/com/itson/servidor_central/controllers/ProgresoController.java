/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidor_central.controllers;

import com.itson.servidorcentral.models.CalificacionDTO;
import com.itson.servidorcentral.websockets.AlertasHandler;
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
        
        String urlMoodleMock = "http://localhost:9090/api/calificaciones";
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            CalificacionDTO respuestaMoodle = restTemplate.getForObject(urlMoodleMock, CalificacionDTO.class);
            
            if (respuestaMoodle != null && respuestaMoodle.materias != null) {
                // Convertimos el objeto a una lista manejable
                List<Map<String, Object>> materias = (List<Map<String, Object>>) respuestaMoodle.materias;
                
                for (Map<String, Object> materia : materias) {
                    double calificacion = Double.parseDouble(materia.get("calificacion").toString());
                    
                    // Si reprueba, enviamos el mensaje Push
                    if (calificacion < 6.0) {
                        String nombreMateria = materia.get("nombre").toString();
                        String mensajeAlerta = "¡Atención! Rendimiento bajo detectado en " + nombreMateria + " (Calificación: " + calificacion + ")";
                        
                        System.out.println("[Servidor Central] Disparando alerta asíncrona por WebSocket...");
                        alertasHandler.enviarAlertaPush(mensajeAlerta);
                    }
                }
            }

            return respuestaMoodle;
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "{\"error\": \"No se pudo conectar con el sistema de calificaciones\"}";
        }
    }
}
