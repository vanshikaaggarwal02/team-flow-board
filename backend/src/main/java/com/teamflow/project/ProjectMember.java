package com.teamflow.project;
import com.teamflow.user.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="project_members") @IdClass(ProjectMemberKey.class)
public class ProjectMember {
 @Id @Column(name="project_id") public Long projectId;
 @Id @Column(name="user_id") public Long userId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) public ProjectRole role;
 @Column(name="joined_at",nullable=false) public Instant joinedAt=Instant.now();
 protected ProjectMember() {}
 public ProjectMember(Long projectId,Long userId,ProjectRole role){this.projectId=projectId;this.userId=userId;this.role=role;}
}
