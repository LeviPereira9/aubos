package lp.boble.aubos.security;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.handler.FilterExceptionHandler;
import lp.boble.aubos.security.apikey.SecurityApiKeyFilter;
import lp.boble.aubos.security.jwt.SecurityJwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SecurityScheme(name = SecurityConfigurations.SECURITY, type= SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme =
        "bearer")
public class SecurityConfigurations {

    public static final String SECURITY = "bearerAuth";

    private static final String[] WHITE_LIST_URLS = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private final SecurityJwtFilter securityJwtFilter;
    private final SecurityApiKeyFilter securityApiKeyFilter;
    private final FilterExceptionHandler filterExceptionHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers(WHITE_LIST_URLS).permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(filterExceptionHandler))
                .addFilterBefore(securityApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
