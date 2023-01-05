package com.java8.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
                                HttpMethod.OPTIONS.name(), HttpMethod.DELETE.name())
                        .allowedHeaders("*")
                        .allowedOrigins("http://localhost:80",
                                "http://localhost:3000",
                                "http://ec2-13-214-191-140.ap-southeast-1.compute.amazonaws.com/");
            }
        };
    }
}
