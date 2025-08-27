package com.authsystem.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

@Component
public class JwtUtil {
    
    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;
    
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;
    
    private SecretKey getSigningKey() {
        logger.info("Secret key: " + secret);
        logger.info("Secret key length: " + secret.length());
        logger.info("Secret key bytes length: " + secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        logger.info("Signing key algorithm: " + key.getAlgorithm());
        return key;
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            logger.info("Extracting claims from token: " + token);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.info("Successfully extracted claims: " + claims);
            return claims;
        } catch (Exception e) {
            logger.severe("Error extracting claims from token: " + e.getMessage());
            logger.severe("Token: " + token);
            throw e;
        }
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateAccessToken(UserDetails userDetails) {
        logger.info("Generating access token for user: " + userDetails.getUsername());
        logger.info("Current secret key: " + secret);
        Map<String, Object> claims = new HashMap<>();
        
        // Add role to claims if user is our custom User entity
        if (userDetails instanceof com.authsystem.entity.User) {
            com.authsystem.entity.User user = (com.authsystem.entity.User) userDetails;
            System.out.println("Adding role to JWT claims: " + user.getRole());
            claims.put("role", user.getRole());
        }
        
        return createToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), refreshTokenExpiration);
    }
    
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        try {
            logger.info("Creating token for subject: " + subject);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.info("Successfully created token: " + token);
            return token;
        } catch (Exception e) {
            logger.severe("Error creating token: " + e.getMessage());
            throw e;
        }
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            logger.info("Validating token for user: " + userDetails.getUsername());
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            logger.info("Token validation result: " + isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            logger.severe("Error validating token: " + e.getMessage());
            return false;
        }
    }
    
    public Boolean validateToken(String token) {
        try {
            logger.info("Validating token (no user details): " + token);
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            logger.info("Token validation successful");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.severe("Error validating token: " + e.getMessage());
            return false;
        }
    }
}

