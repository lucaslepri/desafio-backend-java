package com.itau.desafio.vendas.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.RequestDispatcher; // Importe o RequestDispatcher
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuth implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuth() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String path = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI) != null
                ? request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI).toString()
                : request.getRequestURI();

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Recurso de uso restrito. Autenticação necessária.");
        body.put("path", path);

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}