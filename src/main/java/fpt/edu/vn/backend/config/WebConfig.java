package fpt.edu.vn.backend.config;

import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@Nonnull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://localhost:4200") // Cho phép nguồn Angular
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Authorization", "Pagination");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("https://localhost:4200")); // Cho phép nguồn Angular
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Pagination"));
        configuration.setAllowCredentials(true); // Chắc chắn rằng `credentials` được cho phép
        configuration.setMaxAge(3600L); // Cache cấu hình CORS trong 1 giờ
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}



//@Configuration
//public class WebConfig {
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(@Nonnull CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("https://localhost:4200") // Đảm bảo nguồn của Angular được phép
//                        .allowCredentials(true)
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("*") // Cho phép tất cả các headers
//                        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials","Authorization","Pagination");
//            }
//        };
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Collections.singletonList("https://localhost:4200"));
//        configuration.setAllowedMethods(Collections.singletonList("*"));
//        configuration.setAllowedHeaders(Collections.singletonList("*"));
//        configuration.setExposedHeaders(Collections.singletonList("Pagination"));
//        configuration.setMaxAge(3600L);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
