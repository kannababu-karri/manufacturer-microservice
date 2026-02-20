package com.restful.manufacturer.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.restful.manufacturer.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

    	_LOGGER.info(">>> Inside doFilterInternal manufacturer. <<<");
    	
    	_LOGGER.info(">>> Method: " + request.getMethod());
    	
    	// Allow preflight requests to pass
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            //response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
            String origin = request.getHeader("Origin");
            _LOGGER.info(">>> origin <<<:"+origin);
            if (origin != null) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            }
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            return; // do not continue filter chain
        }
    	
        String authHeader = request.getHeader("Authorization");
        _LOGGER.info(">>> authHeader <<<:"+authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            _LOGGER.info(">>> token <<<:"+token);

            if (jwtUtil.validateToken(token)) {
                chain.doFilter(request, response);
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
