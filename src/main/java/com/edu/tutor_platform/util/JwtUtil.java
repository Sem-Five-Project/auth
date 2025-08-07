package com.authsystem.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;
    
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String extractUsername(String token) {
        System.out.println("Extracting username from token: " + token);
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        System.out.println("Extracting claims: " + claims);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        System.out.println("Extracting all claims from token: " + token);
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    // private Boolean isTokenExpired(String token) {
    //     return extractExpiration(token).before(new Date());
    // }
    private Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            boolean isExpired = expiration.before(now);
            System.out.println("Token expiration time: " + expiration);
            System.out.println("Current time: " + now);
            System.out.println("Is token expired: " + isExpired);
            return isExpired;
        } catch (Exception e) {
            System.out.println("Error checking token expiration: " + e.getMessage());
            return true;
        }
    }
    
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), refreshTokenExpiration);
    }
    
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            System.out.println("Token validation for user " + username + ": " + isValid);
            return isValid;
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }    
    
    // public Boolean validateToken(String token, UserDetails userDetails) {
    //     try {
    //         System.out.println("here 1");
    //         final String username = extractUsername(token);
    //         System.out.println("isTokenExpired(token): "+isTokenExpired(token));
    //         System.out.println("System.out.println(isTokenExpired(token): +isTokenExpired(token)): "+(username.equals(userDetails.getUsername()) && !isTokenExpired(token)));
    //         return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    //     } catch (JwtException | IllegalArgumentException e) {
    //         return false;
    //     }
    // }
    
    public Boolean validateToken(String token) {
        try {
            System.out.println("here 2");

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

