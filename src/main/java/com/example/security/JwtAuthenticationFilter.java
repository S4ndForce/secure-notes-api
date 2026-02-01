package com.example.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint entryPoint;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtAuthenticationEntryPoint entryPoint) {
        this.jwtUtil = jwtUtil;
        this.entryPoint = entryPoint;
    }

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        String header = http.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

           try {
               String email = jwtUtil.validateAndGetEmail(token);
               var auth = new UsernamePasswordAuthenticationToken(
                       email, null, Collections.emptyList()

               );
               // Where Authentication object is created
               SecurityContextHolder.getContext().setAuthentication(auth);

           } catch (Exception ex) {
               SecurityContextHolder.clearContext();

               entryPoint.commence(
                       http,
                       (jakarta.servlet.http.HttpServletResponse) response,
                       new org.springframework.security.authentication.BadCredentialsException(
                               "Invalid JWT", ex
                       ));
               return;
           }
        }

        chain.doFilter(request, response);
    }
}
