package rs.edu.raf.banka1.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import rs.edu.raf.banka1.configuration.authproviders.CustomAuthProvider;
import rs.edu.raf.banka1.filters.JwtFilter;
import rs.edu.raf.banka1.services.CustomerService;
import rs.edu.raf.banka1.services.EmployeeService;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@EnableWebSecurity
@EnableScheduling
@EnableAsync
@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManagerBuilder auth) throws Exception {
        http
                .authorizeHttpRequests((authz) ->
                                authz
//                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                        .requestMatchers(antMatcher("/auth/**"),
                                                antMatcher("/swagger-ui.html"),
                                                antMatcher("/swagger-ui/**"),
                                                antMatcher("/v3/api-docs/**"),
                                                antMatcher("/planning/")).permitAll()
                                        .requestMatchers(antMatcher("/user/activate/**")).permitAll()
                                        .requestMatchers(antMatcher("/permission/**")).permitAll()
                                        .requestMatchers(antMatcher("/employee/activate/**")).permitAll()
                                        .requestMatchers(antMatcher("/employee/newpassword/**")).permitAll()
                                        .requestMatchers(antMatcher("/employee/reset/**")).permitAll()
                                        .requestMatchers(antMatcher("/customer/initialActivation")).permitAll()
                                        .requestMatchers(antMatcher("/customer/activate/**")).permitAll()
                                        .requestMatchers(antMatcher("/customer/newpassword/**")).permitAll()
                                        .requestMatchers(antMatcher("/customer/reset/**")).permitAll()
                                        .requestMatchers(antMatcher("/api/v1/otcTrade/**")).permitAll()
                                        .anyRequest().authenticated()

                )
//                .cors().configurationSource(corsConfigurationSource())

                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Arrays.asList("*"));
                    configuration.setAllowedMethods(Arrays.asList("*"));
                    configuration.setAllowedHeaders(Arrays.asList("*"));
                    return configuration;
                }))
                .csrf(AbstractHttpConfigurer::disable)
//            .csrf()
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Autowired
    public SpringSecurityConfig(EmployeeService employeeService,
                                CustomerService customerService,
                                JwtFilter jwtFilter) {
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public UserDetailsService employeeDetailsService() {
        return this.employeeService;
    }

    @Bean
    public UserDetailsService customerDetailsService() {
        return this.customerService;
    }

//    @Bean
//    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService);
//        authenticationProvider.setPasswordEncoder(passwordEncoder);
//
//        return new ProviderManager(authenticationProvider);
//    }

    @Bean
    public AuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthProvider(employeeDetailsService(), customerDetailsService());
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(customAuthenticationProvider()));
    }
}
