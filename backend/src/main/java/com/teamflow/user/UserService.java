package com.teamflow.user;

import com.teamflow.common.ApiException;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
 private final UserRepository users;
 private final PasswordEncoder passwords;
 public UserService(UserRepository users,PasswordEncoder passwords){this.users=users;this.passwords=passwords;}
 public record ProfileView(Long id,String name,String email){}

 @Transactional
 public ProfileView update(User actor,String name,String email,String currentPassword,String newPassword){
  User user=users.findById(actor.id).orElseThrow(ApiException::notFound);
  String normalizedEmail=email.trim().toLowerCase();
  boolean emailChanged=!normalizedEmail.equals(user.email);
  boolean passwordChanged=newPassword!=null&&!newPassword.isBlank();
  if((emailChanged||passwordChanged)&&(currentPassword==null||!passwords.matches(currentPassword,user.passwordHash)))throw ApiException.badRequest("Enter your current password to change your email or password.");
  if(emailChanged&&users.findByEmail(normalizedEmail).filter(existing->!existing.id.equals(user.id)).isPresent())throw ApiException.conflict("An account with this email already exists.");
  user.name=name.trim();
  user.email=normalizedEmail;
  if(passwordChanged)user.passwordHash=passwords.encode(newPassword);
  user.updatedAt=Instant.now();
  return new ProfileView(user.id,user.name,user.email);
 }
}
