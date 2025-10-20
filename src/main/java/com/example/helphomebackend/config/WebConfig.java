package com.example.helphomebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS 설정 (Next.js와의 연동)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",  // 로컬 개발
                        "https://help-home.vercel.app",  // 프로덕션
                        "https://help-home-25f6ltxs1-rowooncodings-projects.vercel.app",  // 현재 배포
                        "https://*.vercel.app"  // 프리뷰 URL
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
