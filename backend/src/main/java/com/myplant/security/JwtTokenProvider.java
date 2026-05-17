package com.myplant.security;

import com.myplant.entity.User;
import com.myplant.exception.JwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:MyPlantSecretKey1234567890123456}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private int jwtExpirationMs;

    /**
     * Generate JWT token
     */
    public String generateToken(User user) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract user ID from token
     */
    public Long getUserIdFromToken(String token) {

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        try {

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Long.parseLong(claims.getSubject());

        } catch (SecurityException e) {
            throw new JwtException("Invalid JWT signature");

        } catch (MalformedJwtException e) {
            throw new JwtException("Invalid JWT token");

        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT token expired");

        } catch (UnsupportedJwtException e) {
            throw new JwtException("JWT token unsupported");

        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT claims string is empty");
        }
    }

    /**
     * Get email from token
     */
    public String getEmailFromToken(String token) {

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("email", String.class);
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        try {

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (JwtException | IllegalArgumentException e) {

            throw new JwtException(
                    "Token validation failed: " + e.getMessage()
            );
        }
    }

    /**
     * Extract Bearer token
     */
    public String extractTokenFromBearerToken(String bearerToken) {

        if (bearerToken != null &&
                bearerToken.startsWith("Bearer ")) {

            return bearerToken.substring(7);
        }

        return bearerToken;
    }
}