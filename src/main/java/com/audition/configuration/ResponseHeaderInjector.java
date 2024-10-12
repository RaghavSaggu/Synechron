package com.audition.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ResponseHeaderInjector extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Proceed with the filter chain
        filterChain.doFilter(request, response);

        // Read trace and span IDs from request headers (if they exist)
        String traceId = request.getHeader("X-Trace-ID");
        String spanId = request.getHeader("X-Span-ID");

        // Inject them into the response headers if they are present
        if (traceId != null) {
            response.addHeader("X-Trace-ID", traceId);
        }
        if (spanId != null) {
            response.addHeader("X-Span-ID", spanId);
        }
    }
}
