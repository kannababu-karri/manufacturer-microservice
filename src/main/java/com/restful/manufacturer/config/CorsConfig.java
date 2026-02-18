package com.restful.manufacturer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.restful.manufacturer.utils.ILConstants;

@Configuration 
public class CorsConfig { 
	@Bean 
	public WebMvcConfigurer corsConfigurer() { 
		return new WebMvcConfigurer() { 
			@Override public void addCorsMappings(CorsRegistry registry) { 
				registry.addMapping("/**").
				allowedOrigins(ILConstants.ANGULAR_URL).
				allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").
				allowedHeaders("Authorization","Content-Type").
				//allowedHeaders("*").
				allowCredentials(true); 
			} 
		};
	} 
}
