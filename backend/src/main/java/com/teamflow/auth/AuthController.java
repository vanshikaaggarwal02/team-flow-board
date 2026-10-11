package com.teamflow.auth;
import com.teamflow.common.*;
import com.teamflow.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager auth;
 public AuthController(UserRepository users,PasswordEncoder encoder,AuthenticationManager auth){this.users=users;this.encoder=encoder;this.auth=auth;}
 record Register(@NotBlank @Size(max=100) String name,@NotBlank @Email @Size(max=254) String email,@NotBlank @Size(min=10,max=72) String password){}
 record Credentials(@NotBlank @Email String email,@NotBlank String password){}
 record UserView(Long id,String name,String email){}
 @GetMapping("/csrf") Object csrf(CsrfToken token){return java.util.Map.of("token",token.getToken());}
 @PostMapping("/register") @Transactional ResponseEntity<?> register(@Valid @RequestBody Register r){String email=r.email().trim().toLowerCase();if(users.findByEmail(email).isPresent())throw ApiException.conflict("An account with this email already exists.");var u=users.save(new User(r.name().trim(),email,encoder.encode(r.password())));return ResponseEntity.status(201).body(new UserView(u.id,u.name,u.email));}
 @PostMapping("/login") ResponseEntity<?> login(@Valid @RequestBody Credentials c,HttpServletRequest request,HttpServletResponse response){try{Authentication authenticated=auth.authenticate(new UsernamePasswordAuthenticationToken(c.email().trim().toLowerCase(),c.password()));request.getSession(true);request.changeSessionId();SecurityContext context=SecurityContextHolder.createEmptyContext();context.setAuthentication(authenticated);SecurityContextHolder.setContext(context);new HttpSessionSecurityContextRepository().saveContext(context,request,response);return ResponseEntity.ok(users.findByEmail(c.email().trim().toLowerCase()).map(u->new UserView(u.id,u.name,u.email)).orElseThrow());}catch(AuthenticationException e){throw new ApiException(HttpStatus.UNAUTHORIZED,"INVALID_CREDENTIALS","Email or password is incorrect.");}}
 @PostMapping("/logout") ResponseEntity<Void> logout(HttpServletRequest req){var session=req.getSession(false);if(session!=null)session.invalidate();return ResponseEntity.noContent().build();}
 @GetMapping("/me") UserView me(Authentication a){return users.findByEmail(a.getName()).map(u->new UserView(u.id,u.name,u.email)).orElseThrow(ApiException::notFound);}
}
