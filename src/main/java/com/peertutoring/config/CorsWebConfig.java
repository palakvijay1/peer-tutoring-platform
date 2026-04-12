package com.peertutoring.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter (earliest order) for API CORS. Spring's {@code CorsFilter} can still
 * omit {@code Access-Control-Allow-Origin} for {@code Origin: null} ({@code file://}).
 * This reflects the request {@code Origin} on responses and answers {@code OPTIONS}
 * preflight for {@code /api/**} without relying on pattern matching.
 */
@Configuration
public class CorsWebConfig {

    private static final class ReflectOriginApiCorsFilter extends OncePerRequestFilter {

        private static String apiPath(@NonNull HttpServletRequest request) {
            String uri = request.getRequestURI();
            String context = request.getContextPath();
            if (context != null && !context.isEmpty() && uri.startsWith(context)) {
                return uri.substring(context.length());
            }
            return uri;
        }

        @Override
        protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
            return !apiPath(request).startsWith("/api");
        }

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
                @NonNull HttpServletResponse response,
                @NonNull FilterChain filterChain) throws ServletException, IOException {
            String origin = request.getHeader(HttpHeaders.ORIGIN);
            if (origin != null) {
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                response.setHeader(HttpHeaders.VARY, HttpHeaders.ORIGIN);
            }

            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        "GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD");
                String requested = request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        (requested != null && !requested.isBlank()) ? requested : "*");
                response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            filterChain.doFilter(request, response);
        }
    }

    @Bean
    public FilterRegistrationBean<ReflectOriginApiCorsFilter> apiCorsFilterRegistration() {
        FilterRegistrationBean<ReflectOriginApiCorsFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ReflectOriginApiCorsFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
