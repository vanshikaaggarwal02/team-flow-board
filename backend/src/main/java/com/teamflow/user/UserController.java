package com.teamflow.user;

import com.teamflow.common.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
 private final UserService users;
 private final CurrentUser current;
 public UserController(UserService users,CurrentUser current){this.users=users;this.current=current;}
 record ProfileUpdate(@NotBlank @Size(max=100) String name,@NotBlank @Email @Size(max=254) String email,String currentPassword,@Size(min=10,max=72) String newPassword){}

 @PatchMapping("/me") @Transactional
 UserService.ProfileView update(Authentication authentication,@Valid @RequestBody ProfileUpdate request,HttpServletRequest servletRequest,HttpServletResponse response){
  var updated=users.update(current.get(authentication),request.name(),request.email(),request.currentPassword(),request.newPassword());
  if(!updated.email().equals(authentication.getName()) || (request.newPassword()!=null && !request.newPassword().isBlank())){
   servletRequest.changeSessionId();
   var replacement=UsernamePasswordAuthenticationToken.authenticated(updated.email(),null,authentication.getAuthorities());
   SecurityContext context=SecurityContextHolder.createEmptyContext();context.setAuthentication(replacement);SecurityContextHolder.setContext(context);
   new HttpSessionSecurityContextRepository().saveContext(context,servletRequest,response);
  }
  return updated;
 }
}
