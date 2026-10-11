package com.teamflow.project;
import com.teamflow.user.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="projects")
public class Project {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=120) public String name;
 @Column(length=2000) public String description;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="owner_id",nullable=false) public User owner;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
 protected Project() {}
 public Project(String name,String description,User owner){this.name=name;this.description=description;this.owner=owner;}
 public Long getId(){return id;}
}
