/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidorcentral.websockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Chino
 */
@Component
public class AlertasHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sesiones = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sesiones.add(session);
        System.out.println("[WebSocket] App de Padre conectada. ID: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sesiones.remove(session);
        System.out.println("[WebSocket] App de Padre desconectada.");
    }

    // Método para "empujar" la alerta a todos los clientes conectados
    public void enviarAlertaPush(String mensaje) {
        for (WebSocketSession session : sesiones) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(mensaje));
                }
            } catch (Exception e) {
                System.err.println("Error enviando alerta: " + e.getMessage());
            }
        }
    }
}
