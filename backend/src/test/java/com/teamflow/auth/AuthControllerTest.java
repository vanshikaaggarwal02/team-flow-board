package com.teamflow.auth;
import com.teamflow.common.ApiException;
import com.teamflow.user.User;
import com.teamflow.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
 @Mock UserRepository users;
 @Mock PasswordEncoder encoder;
 @Mock AuthenticationManager authentication;
 @InjectMocks AuthController controller;
 @Test void rejectsDuplicateNormalizedEmail(){
  User existing=new User("Existing","user@example.com","hash");
  when(users.findByEmail("user@example.com")).thenReturn(Optional.of(existing));
  ApiException error=assertThrows(ApiException.class,()->controller.register(new AuthController.Register("New"," USER@example.com ","long-enough-password")));
  assertEquals(HttpStatus.CONFLICT,error.status);
  verify(users,never()).save(any());
 }
 @Test void storesOnlyTheEncodedPassword(){
  when(users.findByEmail("user@example.com")).thenReturn(Optional.empty());
  when(encoder.encode("long-enough-password")).thenReturn("bcrypt-hash");
  when(users.save(any(User.class))).thenAnswer(call->call.getArgument(0));
  var response=controller.register(new AuthController.Register("New User","USER@example.com","long-enough-password"));
  ArgumentCaptor<User> saved=ArgumentCaptor.forClass(User.class);
  verify(users).save(saved.capture());
  assertEquals(201,response.getStatusCode().value());
  assertEquals("user@example.com",saved.getValue().email);
  assertEquals("bcrypt-hash",saved.getValue().passwordHash);
  assertNotEquals("long-enough-password",saved.getValue().passwordHash);
 }
 @Test void loginCreatesAuthenticatedSession(){
  User user=new User("User","user@example.com","hash");user.id=7L;
  when(authentication.authenticate(any())).thenReturn(UsernamePasswordAuthenticationToken.authenticated("user@example.com",null,AuthorityUtils.createAuthorityList("ROLE_USER")));
  when(users.findByEmail("user@example.com")).thenReturn(Optional.of(user));
  MockHttpServletRequest request=new MockHttpServletRequest();
  HttpServletResponse response=new MockHttpServletResponse();
  var result=controller.login(new AuthController.Credentials("user@example.com","secret"),request,response);
  assertEquals(200,result.getStatusCode().value());
  assertNotNull(request.getSession(false));
  assertNotNull(request.getSession(false).getAttribute("SPRING_SECURITY_CONTEXT"));
 }
}
