package com.assessment.TaskManagementGradle.config;

import com.assessment.TaskManagementGradle.security.CustomAccessDeniedHandler;
import com.assessment.TaskManagementGradle.security.JwtAuthenticationFilter;
import com.assessment.TaskManagementGradle.security.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize annotations
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for H2 Console
//                .csrf(csrf -> csrf.disable()) // Completely disable CSRF (for debugging)
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/**")) // Disable CSRF for login and register
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Allow H2 Console frames
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/auth/**").permitAll() // Allow public access to authentication routes
                                .requestMatchers("/h2-console/**").permitAll() // Allow H2 Console access
                                .requestMatchers("/users/**").hasRole("ADMIN") // Restrict user management APIs
                                .requestMatchers("/tasks/**").hasAnyRole("ADMIN", "EMPLOYEE") // Example
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions ->
                        exceptions.accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("admin")) // Encode password
//                .roles("ADMIN")
//                .build();
//
//        UserDetails employee = User.builder()
//                .username("emp")
//                .password(passwordEncoder().encode("emp")) // Encode password
//                .roles("EMPLOYEE")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, employee);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/h2-console/**").permitAll() // Allow H2 Console access
//                        .requestMatchers("/tasks/**").hasAnyRole("ADMIN", "EMPLOYEE") // Tasks accessible to mentioned roles
//                        .requestMatchers("/users/**").hasAnyRole("ADMIN") // Tasks accessible to mentioned role
//                        .anyRequest().authenticated() // Secure all other endpoints
//                        //.anyRequest().permitAll() // allow everything else
//
//                )
//                .csrf(csrf -> csrf.disable()) // Disable CSRF for H2 Console
//                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Allow H2 Console frames
//                .formLogin(login -> login.defaultSuccessUrl("/", true)) // Redirect after login
//                .logout(logout -> logout.logoutSuccessUrl("/login").permitAll()); // Redirect after logout
//
//        return http.build();
//    }

