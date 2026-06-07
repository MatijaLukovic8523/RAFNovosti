package com.raf.novosti.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {
    // U starijim verzijama, ključ može biti običan String
    private static final String SECRET_KEY = "e4f9b8c2a1d567890ef234ab1234567890abcdef1234567890abcdef1234567890";

    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email) // Koristimo "set"
                .claim("role", role)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Ovako se potpisuje u starijim verzijama
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            // U starijim verzijama se koristi parseClaimsJws
            Jwts.parser()
                    .setSigningKey(SECRET_KEY) // Ovako se postavlja ključ za proveru
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String extractEmail(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // Pošto smo u generateToken koristili .setSubject(email)
        } catch (Exception e) {
            return null; // Ili baci izuzetak, ali null je lakše za proveru u Filteru
        }
    }
}