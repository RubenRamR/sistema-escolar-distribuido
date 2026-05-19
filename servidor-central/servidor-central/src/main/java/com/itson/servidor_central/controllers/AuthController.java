/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.itson.servidor_central.controllers;

import io.jsonwebtoken.Jwts;
import java.security.Key;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author PC
 */

@RestController
public class AuthController {
    
    // Llave secreta para firmar el JWT (Si usamos docker, esto va en una variable de entorno)
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    @PostMapping("/login")
    public Map<String, String> login() {
        String token = Jwts.builder()
                .setSubject("director_co") // Este es un usuario de prueba
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia de validez
                .signWith(SECRET_KEY)
                .compact();
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}
