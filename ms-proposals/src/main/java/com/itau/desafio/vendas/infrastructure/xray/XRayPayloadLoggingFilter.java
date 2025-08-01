package com.itau.desafio.vendas.infrastructure.xray;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(1)
@Slf4j
@Profile("!prod")
@ConditionalOnProperty(name = "aws.xray.enabled", havingValue = "true")
public class XRayPayloadLoggingFilter extends OncePerRequestFilter {

    public XRayPayloadLoggingFilter() {
        super();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);
        logPayloadsToXRay(requestWrapper, responseWrapper);
        responseWrapper.copyBodyToResponse();
    }

    private void logPayloadsToXRay(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        Segment segment = AWSXRay.getCurrentSegment();
        if (segment == null) {
            return;
        }

        try {
            String requestBody = getContentAsString(request.getContentAsByteArray(), request.getCharacterEncoding());

            Map<String, String> requestDetails = new HashMap<>();
            if (!requestBody.isBlank()) {
                requestDetails.put("request_body", requestBody);
            }
            requestDetails.put("method", request.getMethod());
            requestDetails.put("uri", request.getRequestURI());
            segment.putMetadata("http_request_details", requestDetails);

            String responseBody = getContentAsString(response.getContentAsByteArray(), response.getCharacterEncoding());

            Map<String, Object> responseDetails = new HashMap<>();
            if (!responseBody.isBlank()) {
                responseDetails.put("response_body", responseBody);
            }
            responseDetails.put("status_code", response.getStatus());
            segment.putMetadata("http_response_details", responseDetails);

        } catch (Exception e) {
            log.warn("Falha ao logar payload para o X-Ray. Causa: {}", e.getMessage());
            segment.addException(e);
        }
    }

    private String getContentAsString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) {
            return "";
        }

        String characterEncoding = (charsetName != null) ? charsetName : StandardCharsets.UTF_8.name();

        try {
            return new String(buf, 0, buf.length, characterEncoding);
        } catch (UnsupportedEncodingException ex) {
            log.warn("Codificação de caracteres desconhecida: {}. Logando payload como hexadecimal.",
                    characterEncoding);
            return bytesToHex(buf);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return "HEXADECIMAL_PAYLOAD[" + hexString.toString() + "]";
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}