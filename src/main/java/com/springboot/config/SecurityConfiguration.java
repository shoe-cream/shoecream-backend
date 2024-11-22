package com.springboot.config;


import com.springboot.auth.filter.JwtAuthenticationFilter;
import com.springboot.auth.filter.JwtVerificationFilter;
import com.springboot.auth.handler.MemberAccessDeniedHandler;
import com.springboot.auth.handler.MemberAuthenticationEntryPoint;
import com.springboot.auth.handler.MemberAuthenticationFailureHandler;
import com.springboot.auth.handler.MemberAuthenticationSuccessHandler;
import com.springboot.auth.jwt.JwtTokenizer;
import com.springboot.auth.utils.JwtAuthorityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final JwtAuthorityUtils authorityUtils;

    // 검증 객체에 전달하기 위해 RedisTemplate DI
    private final RedisTemplate<String, Object> redisTemplate;

    public SecurityConfiguration(JwtTokenizer jwtTokenizer, JwtAuthorityUtils authorityUtils, RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())
                .accessDeniedHandler(new MemberAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers(HttpMethod.PATCH, "/orders/*/approve").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PATCH,"/orders/*/reject").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/members").hasRole("ADMIN")
                        .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .antMatchers(HttpMethod.PATCH, "/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.GET, "/members/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/members/**").hasRole("USER")
                        .antMatchers(HttpMethod.POST, "/auth/logout").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/orders").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/orders").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/orders/items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/orders/approve").hasAnyRole( "ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/orders/reject").hasAnyRole( "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders/*/histories").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders/inventories").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders/employees").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders/employees/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/orders/reports").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST,"/items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET,"/items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET,"/items/all").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET,"/items/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH,"/items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE,"/items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST,"/buyer-items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH,"/buyer-items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE,"/buyer-items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET,"/buyer-items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET,"/buyer-items/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET,"/buyer-items/period").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/buyers").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/buyers").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/buyers/all").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/buyers/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/buyers").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/buyers").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/manufacturers").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/manufacturers").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/manufacturers").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/manufacturers/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/manufacturers").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/manufacturers/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/manufacturers/all").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/manufacturers/*/histories").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.POST, "/manufacture-items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/manufacture-items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/manufacture-items/**").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/manufacture-items").hasAnyRole("USER", "ADMIN")
                        .antMatchers(HttpMethod.GET, "/manufacture-items/*/histories").hasAnyRole("USER", "ADMIN")

                        .anyRequest().permitAll()
                );
        //.oauth2Login(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Refresh"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source =new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }

    public class CustomFilterConfigurer  extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception{
            AuthenticationManager authenticationManager =
                    builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter =
                    new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);
            jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());
            JwtVerificationFilter jwtVerificationFilter =
                    new JwtVerificationFilter(jwtTokenizer,authorityUtils,redisTemplate);
            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }
}
