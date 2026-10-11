package com.teamflow.user;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User,Long> { Optional<User> findByEmail(String email); List<User> findAllByEmailIn(Collection<String> emails); }
