package com.teamflow.user;

import java.util.*;
import java.util.stream.IntStream;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("demo")
public class DemoUserSeeder implements ApplicationRunner {
 public static final int USER_COUNT=100;
 private final UserRepository users;
 private final PasswordEncoder passwords;
 private final String demoPassword;
 public DemoUserSeeder(UserRepository users,PasswordEncoder passwords,@Value("${DEMO_USER_PASSWORD:}") String demoPassword){this.users=users;this.passwords=passwords;this.demoPassword=demoPassword;}

 @Override @Transactional
 public void run(ApplicationArguments args){
  List<String> emails=IntStream.rangeClosed(1,USER_COUNT).mapToObj(DemoUserSeeder::emailFor).toList();
  Set<String> existing=new HashSet<>(users.findAllByEmailIn(emails).stream().map(user->user.email).toList());
  if(existing.size()==USER_COUNT)return;
  if(demoPassword.isBlank())throw new IllegalStateException("DEMO_USER_PASSWORD must be set when SPRING_PROFILES_ACTIVE=demo creates demo accounts.");
  String hash=passwords.encode(demoPassword);
  List<User> missing=IntStream.rangeClosed(1,USER_COUNT).filter(i->!existing.contains(emailFor(i))).mapToObj(i->new User(String.format(Locale.ROOT,"Demo User %03d",i),emailFor(i),hash)).toList();
  users.saveAll(missing);
 }
 public static String emailFor(int index){return String.format(Locale.ROOT,"demo.user%03d@example.test",index);}
}
