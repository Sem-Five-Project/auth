package com.edu.tutor_platform.security;

import com.edu.tutor_platform.user.service.UserDetailsServiceImpl;
import com.edu.tutor_platform.user.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        logger.info("Checking if JWT filter should be skipped for path: " + path);
        
        // Skip JWT filter for these paths
        boolean shouldSkip = path.startsWith("/api/actuator/") ||
                            path.startsWith("/actuator/") ||
                            path.startsWith("/api/auth/");
        
        logger.info("Should skip JWT filter: " + shouldSkip);
        return shouldSkip;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        logger.info("Processing JWT authentication filter");
        final String authorizationHeader = request.getHeader("Authorization");
        logger.info("Authorization header: " + authorizationHeader);
        
        String username = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            logger.info("JWT token extracted: " + jwt);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.info("Username extracted from token: " + username);
            } catch (ExpiredJwtException e) {
                logger.warning("JWT Token has expired: " + e.getMessage());
                sendTokenExpiredResponse(response);
                return;
            } catch (JwtException e) {
                logger.severe("Invalid JWT Token: " + e.getMessage());
                sendInvalidTokenResponse(response);
                return;
            } catch (Exception e) {
                logger.severe("Error processing JWT Token: " + e.getMessage());
                sendInvalidTokenResponse(response);
                return;
            }
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Username is not null and no authentication in context, loading user details");
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.info("User details loaded: " + userDetails.getUsername());
                
                logger.info("Validating token for user: " + userDetails.getUsername());
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    logger.info("Token validation successful, setting authentication");
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    logger.warning("Token validation failed for user: " + userDetails.getUsername());
                    sendTokenExpiredResponse(response);
                    return;
                }
            } catch (Exception e) {
                logger.severe("Error during user authentication: " + e.getMessage());
                sendInvalidTokenResponse(response);
                return;
            }
        }
        
        logger.info("Continuing filter chain");
        filterChain.doFilter(request, response);
        logger.info("Filter chain completed");
    }
    
    private void sendTokenExpiredResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "TOKEN_EXPIRED");
        errorResponse.put("message", "Access token has expired. Please refresh your token.");
        errorResponse.put("statusCode", 401);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
    
    private void sendInvalidTokenResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "INVALID_TOKEN");
        errorResponse.put("message", "Invalid or malformed token. Please login again.");
        errorResponse.put("statusCode", 401);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}

