package retepmil.personal.dailysteady.common.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsUtils
import retepmil.personal.dailysteady.common.security.jwt.JwtAuthenticationFilter
import retepmil.personal.dailysteady.common.security.jwt.JwtTokenProvider

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain =
        httpSecurity
            // basic auth 비활성화
            .httpBasic { it.disable() }

            // token을 사용하는 방식이므로 csrf를 비활성화
            .csrf { it.disable() }

            //
            .cors(Customizer.withDefaults())

            // token을 사용하는 방식이므로 STATELESS로 설정
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            // HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정
            .authorizeHttpRequests { request ->
                request
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .requestMatchers("/signin", "/signup").anonymous()
                    .requestMatchers("/member").hasRole("MEMBER")
                    .anyRequest().authenticated()
            }

            // JwtAuthentication을 진행하기 전에 UsernamePasswordAuthenticationFilter를 실행하도록 설정
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )

            // SecurityFilterChain 객체 반환
            .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}