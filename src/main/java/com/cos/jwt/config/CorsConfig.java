package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    //스프링 프레임워크 필터 사용
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //CORS에 대해 참고
        //https://hannut91.github.io/blogs/infra/cors
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //내 서버가 응답할 때 json을 자바스크립트에서 처리할 수 있게 할지 설정하는 것
        config.addAllowedOrigin("*"); //모든 ip에 응답 허용
        config.addAllowedHeader("*"); //모든 header에 응답 허용
        config.addAllowedMethod("*"); //모든 post, get, put, delete 등의 요청을 허용

        source.registerCorsConfiguration("/api/**", config); // /api/**로 들어오는 모든 주소는 config 설정을 따르도록
        
        return new CorsFilter(source);
    }
}
