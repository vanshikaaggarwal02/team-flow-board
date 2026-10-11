package com.teamflow.common;
import com.teamflow.user.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
@Component
public class CurrentUser {
 private final UserRepository users;
 public CurrentUser(UserRepository users){this.users=users;}
 public User get(Authentication auth){return users.findByEmail(auth.getName()).orElseThrow(ApiException::notFound);}
}
