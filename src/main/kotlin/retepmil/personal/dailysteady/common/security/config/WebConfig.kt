package retepmil.personal.dailysteady.common.security.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    // CORS 설정
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8080", "http://localhost:5173")
            .allowedMethods("POST", "GET", "PUT", "DELETE", "HEAD")
            .allowCredentials(true)
    }

}