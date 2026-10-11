package com.teamflow.security;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
 @Bean CookieCsrfTokenRepository csrfRepository(){var r=CookieCsrfTokenRepository.withHttpOnlyFalse();r.setCookiePath("/");return r;}
 @Bean SecurityFilterChain filterChain(HttpSecurity http,CookieCsrfTokenRepository csrf,@Value("${app.frontend-origin}") String origins,ObjectMapper mapper) throws Exception {
  var allowedOrigins=java.util.Arrays.stream(origins.split(",")).map(String::trim).filter(s->!s.isEmpty()).toList();
  http.cors(c->c.configurationSource(req->{var x=new CorsConfiguration();x.setAllowedOrigins(allowedOrigins);x.setAllowedMethods(List.of("GET","POST","PATCH","DELETE","OPTIONS"));x.setAllowedHeaders(List.of("Content-Type","X-XSRF-TOKEN"));x.setAllowCredentials(true);return x;}))
   .csrf(c->c.csrfTokenRepository(csrf).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
   .authorizeHttpRequests(a->a.requestMatchers("/api/v1/auth/register","/api/v1/auth/login","/api/v1/auth/csrf","/api/health","/actuator/health","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll().anyRequest().authenticated())
   .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{res.setStatus(401);res.setContentType("application/json");mapper.writeValue(res.getOutputStream(),Map.of("code","UNAUTHENTICATED","message","Sign in to continue."));}).accessDeniedHandler((req,res,ex)->{res.setStatus(403);res.setContentType("application/json");mapper.writeValue(res.getOutputStream(),Map.of("code","FORBIDDEN","message","You do not have permission to perform this action."));}))
   .formLogin(f->f.disable()).httpBasic(b->b.disable());
  return http.build();
 }
}
