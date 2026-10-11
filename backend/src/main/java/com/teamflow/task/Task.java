package com.teamflow.task;
import com.teamflow.project.Project;
import com.teamflow.user.User;
import jakarta.persistence.*;
import java.time.*;
@Entity @Table(name="tasks")
public class Task {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="project_id",nullable=false) public Project project;
 @Column(nullable=false,length=180) public String title;
 @Column(length=5000) public String description;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public TaskStatus status;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public TaskPriority priority;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="assignee_id") public User assignee;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by",nullable=false) public User creator;
 @Column(name="due_date") public LocalDate dueDate;
 @Version @Column(nullable=false) public long version;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 @Column(name="updated_at",nullable=false) public Instant updatedAt=Instant.now();
 protected Task() {}
 public Task(Project p,String title,String description,TaskPriority priority,User actor){this.project=p;this.title=title;this.description=description;this.priority=priority;this.status=TaskStatus.TODO;this.creator=actor;}
 @PreUpdate public void updated(){updatedAt=Instant.now();}
}
