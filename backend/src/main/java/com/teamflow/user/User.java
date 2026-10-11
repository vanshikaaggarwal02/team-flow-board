package com.teamflow.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="users")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=100) public String name;
 @Column(nullable=false,unique=true,length=254) public String email;
 @Column(name="password_hash",nullable=false,length=100) public String passwordHash;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
 protected User() {}
 public User(String name,String email,String hash){this.name=name;this.email=email;this.passwordHash=hash;}
 public Long getId(){return id;}
 public String getName(){return name;}
}
