/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidor_central.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rramirez
 */
@RestController
@RequestMapping("/api/interacciones")
@CrossOrigin(origins = "*")
public class InteraccionesController {

    @PostMapping("/avalar-tarea")
    public Map<String, String> avalarTarea(@RequestBody Map<String, Object> payload) {
        System.out.println("[Servidor Central] padre avalando tarea: " + payload);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("status", "success");
        respuesta.put("mensaje", "La tarea ha sido avalada y registrada en el sistema.");
        return respuesta;
    }

    @PostMapping("/mensaje")
    public Map<String, String> enviarMensajeProfesor(@RequestBody Map<String, Object> payload) {
        System.out.println("[Servidor Central] Mensaje recibido para profesor: " + payload);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("status", "success");
        respuesta.put("mensaje", "El mensaje fue entregado al buzón del profesor exitosamente.");
        return respuesta;
    }
}
