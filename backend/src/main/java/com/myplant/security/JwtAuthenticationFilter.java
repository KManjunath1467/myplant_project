package com.myplant.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT Authentication Filter
 * 
 * This filter runs for every HTTP request.
 * 
 * Process:
 * 1. Extract JWT token from Authorization header
 * 2. Validate token using JwtTokenProvider
 * 3. Extract user ID from token
 * 4. Create Spring Security authentication object
 * 5. Store in SecurityContext (so Spring knows current user)
 * 6. Pass request to next filter
 * 
 * If token is invalid/missing, request continues but without authentication
 * (Controllers will handle authorization checks)
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extract JWT token from Authorization header
            String jwt = extractJwtFromRequest(request);

            // If token exists and is valid, set authentication
            if (StringUtils.hasText(jwt)) {
                if (tokenProvider.validateToken(jwt)) {
                    // Extract user ID from token
                    Long userId = tokenProvider.getUserIdFromToken(jwt);
                    String email = tokenProvider.getEmailFromToken(jwt);

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userId, 
                                    null, 
                                    new ArrayList<>() // No roles for now
                            );
                    
                    // Add request details for additional context
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Set authentication in security context
                    // Now Spring Security knows the current user
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Pass to next filter in chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * 
     * Header format: "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
     * 
     * @param request the HTTP request
     * @return JWT token or null if not found
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
