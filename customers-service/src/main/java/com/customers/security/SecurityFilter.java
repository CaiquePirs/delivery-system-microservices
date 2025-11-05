package com.customers.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

//public class SecurityFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String header = request.getHeader("Authorization");
//        if (header == null || !header.startsWith("Bearer ")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//
//        String tokenJwt = header.substring(7);
//        Jwt tokenValue = Jwt.withTokenValue(tokenJwt).build();
//
//        String userEmail = tokenValue.getClaim("email");
//
//
//        // Aqui você pode extrair roles, validar claims, etc.
//        // e injetar o Authentication no contexto
//
//        filterChain.doFilter(request, response);
//    }
//}
