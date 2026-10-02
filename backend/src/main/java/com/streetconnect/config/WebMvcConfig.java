package com.streetconnect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;

    @Value("${app.frontend.dir:../frontend/}")
    private String frontendDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose uploaded images
        File uploadFolder = Paths.get(uploadDir).toFile();
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        String uploadAbsolutePath = uploadFolder.getAbsolutePath();
        if (!uploadAbsolutePath.endsWith(File.separator)) {
            uploadAbsolutePath += File.separator;
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath);

        // Static frontend resources — use absolute path from config
        String frontendPath = frontendDir;
        if (!frontendPath.endsWith("/") && !frontendPath.endsWith(File.separator)) {
            frontendPath += "/";
        }
        // Ensure it starts with file: protocol for absolute path
        String frontendLocation = frontendPath.startsWith("file:") ? frontendPath : "file:" + frontendPath;

        registry.addResourceHandler("/**")
                .addResourceLocations(frontendLocation, "classpath:/static/");
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
