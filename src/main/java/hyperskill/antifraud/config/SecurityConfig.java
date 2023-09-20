package hyperskill.antifraud.config;

import hyperskill.antifraud.service.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(CsrfConfigurer::disable)                           // For modifying requests via Postman
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                )
                .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
                .authorizeHttpRequests(requests -> requests                     // manage access
                        .requestMatchers("/actuator/shutdown").permitAll()      // needs to run test
                        .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasRole(Role.ADMINISTRATOR.getRole())
                        .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole(
                                Role.ADMINISTRATOR.getRole(), Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasRole(Role.ADMINISTRATOR.getRole())
                        .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole(Role.ADMINISTRATOR.getRole())
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole(Role.MERCHANT.getRole())
                        .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole(Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/history").hasRole(Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/history/*").hasRole(Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasRole(Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/*").hasRole(Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasRole(Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasRole(Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/*").hasRole(Role.SUPPORT.getRole())
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasRole(Role.SUPPORT.getRole())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                )
                // other configurations
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    };
}
