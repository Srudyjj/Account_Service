package account.security;

import account.model.ROLE;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new RestAuthenticationEntryPoint())) // Handle auth errors
                .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()).disable())
                .headers(headers -> headers.frameOptions().disable()) // For the H2 console
                .authorizeHttpRequests(auth -> auth  // manage access
                        .requestMatchers(toH2Console()).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/shutdown").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/security/events").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyAuthority("ACCOUNTANT", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/admin/user/**").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/user/role").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasAuthority("ACCOUNTANT")
                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasAuthority("ACCOUNTANT")
                        .requestMatchers(HttpMethod.GET, "/api/acct/payments").hasAnyAuthority("ACCOUNTANT", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                        .anyRequest().denyAll()
                )
                .exceptionHandling(handler -> handler.accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
                }))
                .sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}
