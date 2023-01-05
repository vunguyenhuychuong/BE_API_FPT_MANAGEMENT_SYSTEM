package com.java8.tms.config;

import com.java8.tms.common.security.jwt.JwtEntryPoint;
import com.java8.tms.common.security.jwt.JwtTokenFilter;
import com.java8.tms.common.security.userprincipal.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {
    @Autowired
    CustomUserDetailService userDetailsService;

    @Autowired
    private JwtEntryPoint jwtEntryPoint;

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    @Bean
    // như tạo 1 class đế sử dụng đc ở class khác
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DefaultAuthenticationEventPublisher authenticationEventPublisher() {
        return new DefaultAuthenticationEventPublisher();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationEventPublisher(authenticationEventPublisher())
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Enable CORS and disable CSRF
        httpSecurity = httpSecurity.cors().and().csrf().disable();

        /* Set permissions on endpoints */
        httpSecurity
                .authorizeRequests().expressionHandler(webExpressionHandler())
                .antMatchers("/api/v1/auth/login/**", "/api/v1/sign-up/**", "/api/v1/auth/accesstoken", "/css/**",
                        "/js/**", "/img/**", "/lib/**",
                        "/favicon.ico", "/v3/api-docs/**", "/swagger-ui/**",
                        "/error", "/v2/api-docs/**", "/api/v1/change-password", "/api/v1/forgot-password",
                        "/api/v1/verification", "/api/v1/training-class/calendar/**")
                .permitAll()
                .anyRequest().authenticated();
        // Set session management to stateless, session won't be used to store user's
        // state.
        httpSecurity = httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        // Set unauthorized requests exception handler
        httpSecurity = httpSecurity.exceptionHandling()
                .authenticationEntryPoint(jwtEntryPoint)
                .and();

        // Add JWT token filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(
                "FULL_ACCESS_SYLLABUS > CREATE_SYLLABUS\n CREATE_SYLLABUS > MODIFY_SYLLABUS\n MODIFY_SYLLABUS > VIEW_SYLLABUS\n"
                        +
                        "FULL_ACCESS_TRAINING_PROGRAM > CREATE_TRAINING_PROGRAM\n CREATE_TRAINING_PROGRAM > MODIFY_TRAINING_PROGRAM\n MODIFY_TRAINING_PROGRAM > VIEW_TRAINING_PROGRAM\n"
                        +
                        "FULL_ACCESS_CLASS > CREATE_CLASS\n CREATE_CLASS > MODIFY_CLASS\n MODIFY_CLASS > VIEW_CLASS\n" +
                        "FULL_ACCESS_LEARNING_MATERIAL > CREATE_LEARNING_MATERIAL\n CREATE_LEARNING_MATERIAL > MODIFY_LEARNING_MATERIAL\n MODIFY_LEARNING_MATERIAL > VIEW_LEARNING_MATERIAL\n"
                        +
                        "FULL_ACCESS_USER > CREATE_USER\n CREATE_USER > MODIFY_USER\n MODIFY_USER > VIEW_USER > NO_ACCESS_USER\n");
        return roleHierarchy;
    }

    private SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
        return defaultWebSecurityExpressionHandler;
    }

}
