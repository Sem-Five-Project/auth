// // package com.edu.tutor_platform.user.security;
// // // package com.edu.tutor_platform.security;

// // import com.edu.tutor_platform.user.service.UserDetailsServiceImpl;
// // import com.edu.tutor_platform.user.util.JwtUtil;
// // import jakarta.servlet.FilterChain;
// // import jakarta.servlet.ServletException;
// // import jakarta.servlet.http.HttpServletRequest;
// // import jakarta.servlet.http.HttpServletResponse;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// // import org.springframework.security.core.context.SecurityContextHolder;
// // import org.springframework.security.core.userdetails.UserDetails;
// // import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// // import org.springframework.stereotype.Component;
// // import org.springframework.web.filter.OncePerRequestFilter;

// // import java.io.IOException;
// // import java.util.logging.Logger;

// // @Component
// // public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
// //     private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());
    
// //     @Autowired
// //     private JwtUtil jwtUtil;
    
// //     @Autowired
// //     private UserDetailsServiceImpl userDetailsService;
    
// //     @Override
// //     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
// //             throws ServletException, IOException {
        
// //         logger.info("Processing JWT authentication filter");
// //         final String authorizationHeader = request.getHeader("Authorization");
// //         logger.info("Authorization header: " + authorizationHeader);
        
// //         String username = null;
// //         String jwt = null;
        
// //         if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
// //             jwt = authorizationHeader.substring(7);
// //             logger.info("JWT token extracted: " + jwt);
// //             try {
// //                 username = jwtUtil.extractUsername(jwt);
// //                 logger.info("Username extracted from token: " + username);
// //             } catch (Exception e) {
// //                 logger.severe("Cannot get JWT Token: " + e.getMessage());
// //                 e.printStackTrace();
// //             }
// //         }
        
// //         if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
// //             logger.info("Username is not null and no authentication in context, loading user details");
// //             UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
// //             logger.info("User details loaded: " + userDetails.getUsername());
            
// //             logger.info("Validating token for user: " + userDetails.getUsername());
// //             if (jwtUtil.validateToken(jwt, userDetails)) {
// //                 logger.info("Token validation successful, setting authentication");
// //                 UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
// //                         new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
// //                 usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
// //                 SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
// //             } else {
// //                 logger.warning("Token validation failed for user: " + userDetails.getUsername());
// //             }
// //         }
        
// //         logger.info("Continuing filter chain");
// //         filterChain.doFilter(request, response);
// //         logger.info("Filter chain completed");
// //     }
// // }

// package com.edu.tutor_platform.user.security;

// import com.edu.tutor_platform.user.service.UserDetailsServiceImpl;
// import com.edu.tutor_platform.user.util.JwtUtil;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import java.io.IOException;
// import java.util.logging.Logger;

// @Component
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

//     private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Autowired
//     private UserDetailsServiceImpl userDetailsService;

//     // List of public endpoints that do not require JWT
//     private static final String[] PUBLIC_ENDPOINTS = {
//             "/api/auth/login",
//             "/api/auth/register"
//     };

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {

//         String path = request.getServletPath();

//         // Skip JWT validation for public endpoints
//         // for (String endpoint : PUBLIC_ENDPOINTS) {
//         //     if (path.equals(endpoint)) {
//         //         filterChain.doFilter(request, response);
//         //         return;
//         //     }
//         // }
//         for (String endpoint : PUBLIC_ENDPOINTS) {
//     if (path.startsWith(endpoint)) {
//         logger.info("Skipping JWT filter for public endpoint: " + path);
//         filterChain.doFilter(request, response);
//         return;
//     }
// }


//         logger.info("Processing JWT authentication filter for: " + path);

//         final String authorizationHeader = request.getHeader("Authorization");
//         String username = null;
//         String jwt = null;

//         if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//             jwt = authorizationHeader.substring(7);
//             logger.info("JWT token extracted: " + jwt);
//             try {
//                 username = jwtUtil.extractUsername(jwt);
//                 logger.info("Username extracted from token: " + username);
//             } catch (Exception e) {
//                 logger.severe("Cannot extract username from JWT: " + e.getMessage());
//             }
//         }

//         if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//             UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

//             if (jwtUtil.validateToken(jwt, userDetails)) {
//                 UsernamePasswordAuthenticationToken authToken =
//                         new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                 SecurityContextHolder.getContext().setAuthentication(authToken);
//                 logger.info("Authentication set for user: " + username);
//             } else {
//                 logger.warning("Invalid JWT token for user: " + username);
//             }
//         }

//         filterChain.doFilter(request, response);
//     }
// }
