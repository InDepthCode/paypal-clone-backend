package com.paypal.user_service.util;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestfilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestfilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String userName = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try{
                // we have extracted user name
                userName = jwtUtil.extractUsername(jwtToken);
            }catch ( Exception e){
                System.out.println("JWT parsing failed: " + e.getMessage());
            }
        }
        // we have validated the authentication
        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if(jwtUtil.validateToken(jwtToken, userName)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, null, null);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
    }
}
