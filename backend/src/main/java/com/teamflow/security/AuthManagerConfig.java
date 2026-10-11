package com.teamflow.security;
import com.teamflow.user.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.*;
@Configuration
public class AuthManagerConfig {
 @Bean UserDetailsService userDetailsService(UserRepository users){return email->users.findByEmail(email).map(u->User.withUsername(u.email).password(u.passwordHash).roles("USER").build()).orElseThrow(()->new UsernameNotFoundException("User not found"));}
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
}
