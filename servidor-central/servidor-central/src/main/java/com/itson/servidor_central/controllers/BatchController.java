/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidor_central.controllers;

import com.itson.servidor_central.SincronizadorBatchService;
import com.itson.servidor_central.models.CalificacionDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = "*")
public class BatchController {

    private final SincronizadorBatchService sincronizador;

    public BatchController(SincronizadorBatchService sincronizador) {
        this.sincronizador = sincronizador;
    }

    @PostMapping("/api/batch/sincronizar")
    public String sincronizarConSCE() {
        System.out.println("[Batch] Recibida petición de sincronización batch...");

        RestTemplate restTemplate = new RestTemplate();
        String urlMoodle = "http://localhost:9090/api/calificaciones";

        try {
            CalificacionDTO datos = restTemplate
                    .getForObject(urlMoodle, CalificacionDTO.class);

            // datos.materias sigue siendo Object — el Service hace el cast interno
            sincronizador.enviarLote(datos.materias, datos.nombre);
            return "{\"status\": \"lote enviado al SCE\"}";

        } catch (Exception e) {
            System.err.println("[Batch] Error: " + e.getMessage());
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}