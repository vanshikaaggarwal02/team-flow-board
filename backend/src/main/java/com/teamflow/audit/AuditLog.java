package com.teamflow.audit;
import com.teamflow.task.Task;
import com.teamflow.project.Project;
import com.teamflow.user.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="audit_logs")
public class AuditLog {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="project_id",nullable=false) public Project project;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="task_id",nullable=false) public Task task;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="actor_id",nullable=false) public User actor;
 @Column(nullable=false,length=40) public String action;
 @Column(name="old_value",columnDefinition="text") public String oldValue;
 @Column(name="new_value",columnDefinition="text") public String newValue;
 @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
 protected AuditLog(){}
 public AuditLog(Task task,User actor,String action,String oldValue,String newValue){this.project=task.project;this.task=task;this.actor=actor;this.action=action;this.oldValue=oldValue;this.newValue=newValue;}
}
