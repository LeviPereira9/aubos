package lp.boble.aubos.security;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.handler.FilterExceptionHandler;
import lp.boble.aubos.security.apikey.SecurityApiKeyFilter;
import lp.boble.aubos.security.jwt.SecurityJwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
public class SecurityConfigurations {

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
                                .requestMatchers(HttpMethod.POST, "/api/v1/user/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/user/login").permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(filterExceptionHandler))
                .addFilterBefore(securityApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
