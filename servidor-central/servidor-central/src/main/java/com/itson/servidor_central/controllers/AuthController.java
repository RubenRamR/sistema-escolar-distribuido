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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author PC
 */
@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @PostMapping("/api/auth/login")
    public Map<String, String> login(@RequestBody Map<String, String> credenciales) {

        String usuario = credenciales.get("usuario");

        String token = Jwts.builder()
                .setSubject(usuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SECRET_KEY)
                .compact();

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("mensaje", "Autenticación exitosa. Bienvenido, " + usuario);

        return response;
    }
}
